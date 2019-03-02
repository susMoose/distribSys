package cs455.overlay.wireformats;

import java.io.IOException;

public class TrafficSummary extends Message {
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	
	public TrafficSummary (String ipAddr, int portNumber, int numberSent, long sentSum, int numberReceived, long reciptSum, int numberRelayed) throws IOException {
		this.messageType = getMessageNumber("TRAFFIC_SUMMARY");
		this.payload = generateRandomPayload();	
		this.messageBytes = trafficSummaryMarshaller(payload, ipAddr, portNumber, numberSent, sentSum, numberReceived, reciptSum, numberRelayed);
	}
	
	
}
