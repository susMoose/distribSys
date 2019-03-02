package cs455.overlay.wireformats;

import java.io.IOException;

public class RegisterResponse extends Message{
	//	Status Code (byte): SUCCESS or FAILURE
	//	Additional Info (String):

	//	“Registration request
	//	successful. The number of messaging nodes currently constituting the overlay is (5)”. If
	//	the registration was unsuccessful, the message from the registry should indicate why the request was
	//	unsuccessful.
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	/** Constructor */
	public RegisterResponse(String statusCode, String info) throws IOException{
		this.messageType = getMessageNumber("REGISTER_RESPONSE");
		this.payload = generateRandomPayload();	
		this.messageBytes = registerResponseMarshaller(payload, statusCode, info );
	}
	
}
