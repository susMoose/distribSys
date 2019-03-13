package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ThreadPoolManager implements Runnable{
	private ServerSocketChannel ssChannel;
	private Selector selector;
	private final TaskQueue taskQ;

	/** ThreadPoolManager Constructor */
	public ThreadPoolManager(int port_number, int thread_pool_size, int batch_size, double batch_time) throws IOException {
		//Creating TaskQueue
		taskQ = new TaskQueue(batch_size, batch_time);
		Thread taskTimer = new Thread(taskQ);	
		taskTimer.start();

		//Creating input channel
		selector = Selector.open();	
		ssChannel = ServerSocketChannel.open();
		ssChannel.bind(new InetSocketAddress(port_number));
		ssChannel.configureBlocking(false); 
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);	//registers ssChannel w/ selector to accept new connections

		//Creating threadPool
		createThreadPool(thread_pool_size);		 
	}

	/** Creates a number of worker threads as specified by the parameter thread_pool_size */
	private void createThreadPool(int thread_pool_size) {
		System.out.println("Creating Thread Pool");
		for(int i = 0; i < thread_pool_size; i++) {
			Thread workingThread = new Thread(new WorkerThread(taskQ, ssChannel)); 
			workingThread.start();		
		}
	}

	/** On receipt of a ping, put Selection key in taskQueue */
	public void run() {
		while(true)  {
			// Blocks here
			try { selector.selectNow(); } catch (IOException e) {e.printStackTrace();}
			// Key(s) are ready
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			// Loop over ready keys
			Iterator<SelectionKey> iter = selectedKeys.iterator();

			while (iter.hasNext()) {
				
				SelectionKey key = (SelectionKey)iter.next();
				if (key.isAcceptable() && key.isValid()) {
					taskQ.addTask(key);
				}
				
				else if (key.isReadable() && key.isValid()) {
//					if( key.attachment() == null) {	//if not true
//						key.attach("not null");	 // Attaching signal meaning in progress
						taskQ.addTask(key);
//					}
				}
				iter.remove();
			}
		}
	}
}







