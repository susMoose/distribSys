package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedList;

public class ThreadPoolManager {
	private ServerSocketChannel ssChannel;
	private LinkedList<LinkedList<byte[]>> workList;
	
	/* ThreadPoolManager Constructor */
	public ThreadPoolManager(int port_number, int thread_pool_size) throws IOException {
		Selector selector = Selector.open();	

		//Creating input channel
		ssChannel = ServerSocketChannel.open();
		ssChannel.bind(new InetSocketAddress(port_number));
		ssChannel.configureBlocking(false); 
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);	//registers ssChannel w/ selector to accept new connections
	}

	
	
}







