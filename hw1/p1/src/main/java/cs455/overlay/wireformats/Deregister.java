package cs455.overlay.wireformats;

import java.io.IOException;

public class Deregister extends Message {
	// This is sent right before a messaging node leaves the overlay
	private long payload;
	private byte[] messageBytes;
	private int messageType;	
	
	/** Constructor */
	public Deregister(String ipAddr, int portNumber) throws IOException{
			this.messageType =getMessageNumber("DEREGISTER_REQUEST");
			this.payload = generateRandomPayload();		
			this.messageBytes = registerMarshaller(payload, ipAddr, portNumber);
		}
}
	
