package cs455.scaling.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import cs455.scaling.hash.Hash;

public class WorkerThread implements Runnable{
	private TaskQueue taskQ = null ;
	private ServerSocketChannel ssChannel;


	/** Constructor gets reference to taskQ 
	 * @param ssChannel */
	public WorkerThread(TaskQueue task_queue, ServerSocketChannel ssc) {
		taskQ = task_queue;
		ssChannel = ssc;
	}

	/** Constantly waiting to perform a task when available */
	public void run() {
		while(true) {
			ThreadPool.addToPool(this);
			ConcurrentLinkedQueue<SelectionKey>  myTaskBatch = taskQ.waitForTasks();	// waits for input from queue
			//Once here the thread has a batch to break down and perform the actions
			handleTask(myTaskBatch);
		}
	}

	/** Gets each task key from the batch and directs where it goes to be handled */
	private void handleTask ( ConcurrentLinkedQueue<SelectionKey>  myTaskBatch) {
		for (int i = 0; i < myTaskBatch.size(); i++ ) {
			SelectionKey key = myTaskBatch.poll();
			if (key.isAcceptable()) acceptConnection(key);
			else read(key);
		}
	}

	/** Registers the key's channel and configures the channel to reading */
	public void acceptConnection(SelectionKey key) {
		try {
			SocketChannel client = ssChannel.accept();
			if (client == null) return;
			client.configureBlocking(false);
			client.register(key.selector(), SelectionKey.OP_READ );
			
			ServerStatistics.incrementActiveConnections(client.keyFor(key.selector()));
//			System.out.println("<3 Successfully accepted connection\n");
		} catch (IOException e) {e.printStackTrace();}
	}

	/** Reads the message from the key's socketChannel */
	private void read(SelectionKey key) {

		SocketChannel client = (SocketChannel) key.channel();
		try {
			ByteBuffer buffer = ByteBuffer.allocate(8000);
			int size = client.read(buffer);	// reading from channel into buffer 
			if(size != 0) {
				byte[] bytesRead = buffer.array();
				String messageHash = Hash.SHA1FromBytes(bytesRead);
				ServerStatistics.incrementNumberProcessed(key);
				buffer.flip();			// Flip + rewind the buffer to be writable again
				buffer = ByteBuffer.wrap(messageHash.getBytes());
				client.write(buffer);			// writes computed hash back
			}
		} catch (IOException e) { e.printStackTrace(); }
		key.attach(null);
	}
}
