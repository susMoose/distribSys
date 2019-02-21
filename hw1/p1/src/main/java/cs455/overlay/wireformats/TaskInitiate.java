package cs455.overlay.wireformats;

import java.io.IOException;

public class TaskInitiate extends Message{
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	public TaskInitiate(int rounds) throws IOException  {
		this.messageType = getMessageNumber("TASK_INITIATE");
		this.payload = generateRandomPayload();	
		this.messageBytes = taskInitiateMarshaller(payload, rounds);		
	}
}
