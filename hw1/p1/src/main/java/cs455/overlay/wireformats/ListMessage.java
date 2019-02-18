package cs455.overlay.wireformats;

import java.io.IOException;

public class ListMessage extends Message{
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	public ListMessage(MessagingNodesList mnl, int nNeededConnections) throws IOException {
		this.messageType = getMessageNumber("MESSAGING_NODES_LIST");
		this.payload = generateRandomPayload();	
		this.messageBytes = mNodesListMarshaller(payload, nNeededConnections,mnl );
	}
}
