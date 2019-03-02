package cs455.overlay.wireformats;

import java.io.IOException;

public class PullTrafficRequest extends Message{
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	public PullTrafficRequest () throws IOException {
		this.messageType = getMessageNumber("PULL_TRAFFIC_SUMMARY");
		this.payload = generateRandomPayload();	
		this.messageBytes = pullTrafficMarshaller(payload);
	}
}
