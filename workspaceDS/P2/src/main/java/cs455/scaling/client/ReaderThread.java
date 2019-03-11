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
			if(!hashList.contains(response)) { // Executes if the code was not one previously sent
				if(!response.equals("75a289eec0a33499fbaa6e89081c0efa604fbdd4")){
					System.out.println("ERROR: incorrect hashcode ");
					try {
						System.out.println("received: " + response);
						Thread.sleep(10002);
						hashList.printEveryone();
					} catch (InterruptedException e) {e.printStackTrace();}
				}else {System.out.println("weird repeated code ");}
			}else {System.out.println("Approved hash response");}			
		}
	}
}
