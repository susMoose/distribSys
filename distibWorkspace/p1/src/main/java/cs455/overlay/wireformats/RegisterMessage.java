package cs455.overlay.wireformats;

import java.io.IOException;

public class RegisterMessage extends Message {
	private int messageType;
	private long payload;
	private byte[] messageBytes;

	/** Constructor */
	public RegisterMessage(String ipAddr, int portNumber) throws IOException{
		this.messageType = getMessageNumber("REGISTER_REQUEST");
		this.payload = generateRandomPayload();		
		this.messageBytes = registerMarshaller(payload, ipAddr, portNumber);
	}
	
	
	
}