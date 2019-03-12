package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

import cs455.scaling.hash.Hash;

public class SenderThread implements Runnable {
	private SocketChannel channel;
	private int messageRate;
	private ByteBuffer buffer;
	private Hashcodes hashList;

	/** SenderThread Constructor */
	public SenderThread(SocketChannel socket_channel, int message_rate, Hashcodes list_of_hashes) throws IOException {
		channel = socket_channel;
		messageRate = message_rate;
		buffer = ByteBuffer.allocate(8000);
		hashList = list_of_hashes;
	}

	/** Writing messages to buffer then to the channel for the server */
	public void run() {
		byte[] payload = new byte[8000];
		while (channel.isConnected()){
			new Random().nextBytes(payload); 	// Generates random bytes and places them into byte array.
			
			String h = Hash.SHA1FromBytes(payload);
			hashList.add(h);					// storing hash
//			System.out.println(" Sent hash message of "+ h);
			buffer.rewind();
			buffer = ByteBuffer.wrap(payload);	// puts random payload into buffer
			try {		
				System.out.print(".");
				// Writing to channel 
				channel.write(buffer);
				// Sleeping for specified time 
				Thread.sleep(1000/messageRate);
				
			} catch (IOException e) { e.printStackTrace();
			} catch (InterruptedException e) { e.printStackTrace();}
			
			ClientStatistics.incrementNumberSent();
		}
		System.out.println("channel is disconnected");
	}
	
	
}
