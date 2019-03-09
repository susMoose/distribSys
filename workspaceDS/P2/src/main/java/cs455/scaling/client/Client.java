package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Client {
	private ServerSocketChannel ssChannel;
	private SocketChannel client;
    private static ByteBuffer buffer;

	
	/*Client Constructor*/
	public Client(String serverHost, int serverPort, int messageRate) throws IOException {
		// Creating our ServerSocketChannel to receive connections on 
		ssChannel = ServerSocketChannel.open();
		ssChannel.accept();
		ssChannel.configureBlocking(false);
		findSocket();
		
		// Connecting to the server
        client = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
        
        // Create buffer 
        buffer = ByteBuffer.allocate(256);
        
        //Create message to be sent 
        
		
		
		
	}
	
	
	private void findSocket() {
		int portFinder = 1025;
		while(true) {
			try {	
				ssChannel.socket().bind(new InetSocketAddress(portFinder));
				break;	
			} catch(IOException e) { portFinder++; }
		}
	}
	
	
	public static void main(String[] args) {
//		String serverHost = args[0];
//		int serverPort =Integer.parseInt(args[1]);
//		int R =Integer.parseInt(args[2]);		//typically between 2-4
//		new Client(serverHost, serverPort, messageRate);
		
		try {
			new Client("mercury.cs.colostate.edu", 3222 , 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
