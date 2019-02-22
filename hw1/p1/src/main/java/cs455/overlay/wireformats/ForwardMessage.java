package cs455.overlay.wireformats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;


public class ForwardMessage extends Message{
	private int messageType;	
	private long payload;
	private byte[] messageBytes;

	/** Constructor 
	 * @param payload2 **/
	public ForwardMessage(ArrayList<String> nextSteps, long pyload) throws IOException {
		this.messageType = getMessageNumber("FORWARD");
		this.payload = pyload;
		this.messageBytes =forwardMarshaller(payload,nextSteps);
	}
	public ForwardMessage(ArrayList<String> nextSteps) throws IOException {
		this.messageType = getMessageNumber("FORWARD");
		this.payload = generateRandomPayload();
		this.messageBytes =forwardMarshaller(payload,nextSteps);
	}
}
