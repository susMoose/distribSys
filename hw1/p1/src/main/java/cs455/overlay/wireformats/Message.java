package cs455.overlay.wireformats;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom; //ASK 

public class Message {
	private long payload; 
	private byte[] marshalledBytes;
	private int messageType;

	public void setMessageType (int mType) {
		this.messageType = mType;
	}

	public long generateRandomPayload() {
		long payload = ThreadLocalRandom.current().nextLong(-2147483648L, 2147483647L);
		return payload; 
	}

	// inner Message ends up being format: int ipAddress length,  byte[] ipAddress, int portNumber, long payload
	public byte[] registerMarshaller(long pyld, String ipAddr, int portNumber) throws IOException {
		this.payload = pyld;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		byte[] ipAddress = ipAddr.getBytes();
		int ipAddressLength = ipAddress.length;

		dout.writeInt(ipAddressLength);
		dout.write(ipAddress);
		dout.writeInt(portNumber);		
		dout.writeLong(payload);
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	//Sends Status byte[] length, then the status byte[], then the info array length, then the byte [] of info
	public byte[] registerResponseMarshaller(long pyld, String statusCode, String additionalInfo ) throws IOException {
		this.payload = pyld;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));

		byte[] status = statusCode.getBytes();
		int statLength = status.length;
		byte[] info = additionalInfo.getBytes();
		int infoLength = info.length;

		System.out.println("|> Node's registry status: " + statusCode +", Payload Sent: " + payload);

		dout.writeInt(statLength); 
		dout.write(status);
		dout.writeInt(infoLength);
		dout.write(info);
		dout.writeLong(payload);
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	public byte[] deregisterMarshaller() {
		return marshalledBytes;
	}

	public byte[] deregisterResponseMarshaller() {
		return marshalledBytes;
	}

	// Marshall list MessagingNodesList
	public byte[] mNodesListMarshaller(long pyld, int nNeededConnections, MessagingNodesList mnl) throws IOException {
		this.payload = pyld;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		//Writing byte array for my object
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(mnl.getList());
		out.flush();
		byte[] listBytes = bos.toByteArray();
		bos.close();
		int listBytesLength = listBytes.length;
		
		dout.writeInt(nNeededConnections);
		dout.writeInt(listBytesLength);
		dout.write(listBytes);
		dout.writeLong(payload);
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
	}

	public byte[] linkWeightsMarshaller() {
		return marshalledBytes;
	}

	public byte[] taskInitiateMarshaller() {
		return marshalledBytes;
	}

	public byte[] TaskCompleteMarshaller() {
		return marshalledBytes;
	}

	public byte[] pullTrafficMarshaller() {
		return marshalledBytes;
	}

	public byte[] trafficSummaryMarshaller() {
		return marshalledBytes;
	}

	// Returns the integer representation of the messageType
	public int getMessageNumber(String message) {
		if(message.contentEquals( "REGISTER_REQUEST")) messageType = 0;
		else if(message.contentEquals( "REGISTER_RESPONSE")) messageType = 1;
		else if(message.contentEquals( "DEREGISTER_REQUEST")) messageType = 2;
		else if(message.contentEquals( "DEREGISTER_RESPONSE")) messageType = 3;
		else if(message.contentEquals( "MESSAGING_NODES_LIST")) messageType = 4;
		else if(message.contentEquals( "Link_Weights")) messageType = 5;
		else if(message.contentEquals( "TASK_INITIATE")) messageType = 6;
		else if(message.contentEquals( "TASK_COMPLETE")) messageType = 7;
		else if(message.contentEquals( "PULL_TRAFFIC_SUMMARY")) messageType = 8;
		else if(message.contentEquals( "TRAFFIC_SUMMARY")) messageType = 9;
		else {System.out.println("message type does not exist.");}
		return messageType;
	}
	public long getPayload() {return payload;}
	public byte[] getByteArray() { return marshalledBytes; }
	public int getMessageType() { return messageType; }




}
