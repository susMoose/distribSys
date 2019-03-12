package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ReaderThread implements Runnable {
	private SocketChannel channel;
	private ByteBuffer buffer;
	private Hashcodes hashList;

	/** Reader Thread Constructor */
	public ReaderThread(SocketChannel socket_channel, Hashcodes list_of_hashes) {
		channel = socket_channel;
		buffer = ByteBuffer.allocate(8000);
		hashList = list_of_hashes;
	}

	/** Reads any incoming messages from buffer */
	public void run() {
		while (true){
			String response = null;	
			try {	
				buffer.clear();
				channel.read(buffer);
				response = new String(buffer.array()).trim();
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
			// Executes if the code was not one that was previously sent
			if(!hashList.contains(response)) System.out.println("ERROR: incorrect hashcode ");
			ClientStatistics.incrementNumberReceived();
		}
	}
}
