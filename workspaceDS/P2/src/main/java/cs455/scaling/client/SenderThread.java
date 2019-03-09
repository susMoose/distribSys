package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SenderThread {
	
	/* SenderThread Constructor */
	public SenderThread(String host_address, int port_number) throws IOException {
		
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(host_address, port_number));
		
		
		//store selection key 
//		socketChannel.register(selector, SelectionKye.OP_CONNECT);
		
	}

}
