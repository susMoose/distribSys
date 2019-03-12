package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
	
	/** Client Constructor connects to the server then spawns reader and sender threads */
	public Client(String sHost, int sPort, int mRate) throws IOException {
		SocketChannel channel= SocketChannel.open(new InetSocketAddress(sHost, sPort));
		channel.finishConnect();
		Hashcodes hashList = new Hashcodes();

		Thread statsThread = new Thread(new ClientStatistics()); // Client Stats thread
		statsThread.start();
		
        Thread senderThread = new Thread(new SenderThread(channel, mRate, hashList));
        senderThread.start();
        
        Thread readerThread = new Thread(new ReaderThread(channel, hashList));
        readerThread.start();
        
        
	}
	
	
	public static void main(String[] args) throws IOException {
		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		int messageRate =Integer.parseInt(args[2]);		// typically between 2-4
		new Client(serverHost, serverPort, messageRate);
	}
}
