package cs455.overlay.wireformats;

import java.io.IOException;

public class DeregisterResponse extends Message{
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	/** Constructor */
	public DeregisterResponse(String statusCode, String info) throws IOException{
		this.messageType = getMessageNumber("DEREGISTER_RESPONSE");
		this.payload = generateRandomPayload();	
		this.messageBytes = registerResponseMarshaller(payload, statusCode, info );
	}
}
