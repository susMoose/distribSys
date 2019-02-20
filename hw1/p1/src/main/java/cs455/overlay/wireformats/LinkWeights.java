package cs455.overlay.wireformats;

import java.io.IOException;

public class LinkWeights extends Message {
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	public LinkWeights(MessagingNodesList mnl, int nNeededConnections,int numNodes) throws IOException {
		this.messageType = getMessageNumber("Link_Weights");
		this.payload = generateRandomPayload();	
		this.messageBytes = linkWeightsMarshaller(payload, nNeededConnections,mnl, numNodes );		
	}
}
