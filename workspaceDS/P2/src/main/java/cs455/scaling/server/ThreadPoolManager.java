package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager implements Runnable{
	private ServerSocketChannel ssChannel;
	private Selector selector;
	private Queue workQueue;		// This queue contains units of work, each unit of work  is a list of packets with a max length of batch size

	/* ThreadPoolManager Constructor */
	public ThreadPoolManager(int port_number, int thread_pool_size, int batch_size) throws IOException {
		selector = Selector.open();	
		createThreadPool(thread_pool_size);		//creating threads 
		workQueue = new LinkedList<LinkedList<byte[]>>();  // creating work queue

		//Creating input channel
		ssChannel = ServerSocketChannel.open();
		ssChannel.bind(new InetSocketAddress(port_number));
		ssChannel.configureBlocking(false); 
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);	//registers ssChannel w/ selector to accept new connections
	}
	
	
	/* Creates a number of worker threads as specified by the parameter thread_pool_size */
	private void createThreadPool(int thread_pool_size) {
		for(int i = 0; i < thread_pool_size; i++) {
			Thread workingThread = new Thread(new WorkerThread()); 
			workingThread.start();		
		}
	}

	/* Accepts incoming connections */
	private void accept(SelectionKey key) {	
		WorkerThread worker = ThreadPool.getFromPool();		//removes a worker thread from the pool
		worker.notify();	//alerts the thread that it is being used 
		
		
	}
	
	private void read(SelectionKey key) {}
	
	
	
	

	public void run() {
		Iterator keys = selector.selectedKeys().iterator();		//The selection keys are objs that conatin references to channels(sockets)
		while(true) 
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey)keys.next();
				if (key.isAcceptable()) {	 // will return true if that channel associated with that key has an incoming connection that it can accept
					this.accept(key);
				} else if (key.isReadable()) {
					this.read(key);
				}
			}
		
	}

}







