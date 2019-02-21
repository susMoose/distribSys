package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom; //ASK 

import cs455.overlay.dijkstra.ShortestPath.Vertex;
import cs455.overlay.wireformats.MessagingNodesList.NodeLink;

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
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		this.payload = pyld;
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
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		this.payload = pyld;

		byte[] status = statusCode.getBytes();
		int statLength = status.length;
		byte[] info = additionalInfo.getBytes();
		int infoLength = info.length;

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
	//As the message contents are the same I just reuse the  registerMarshallers
	public byte[] deregisterMarshaller(long pyld, String ipAddr, int portNumber) throws IOException {
		return registerMarshaller(payload, ipAddr, portNumber);
	}
	public byte[] deregisterResponseMarshaller(long pyld, String statusCode, String additionalInfo ) throws IOException {
		return registerResponseMarshaller(payload, statusCode, additionalInfo );
	}

	// Marshall list MessagingNodesList
	public byte[] mNodesListMarshaller(long pyld, int nNeededConnections, MessagingNodesList mnl, int totalCr) throws IOException {
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		this.payload = pyld;
		
		ByteArrayOutputStream box=new ByteArrayOutputStream();;
		DataOutputStream outputBox=new DataOutputStream(box);
		byte[] listBytes = null, ip = null;
		int i = 0, p = 0;
		for(NodeLink nod: mnl.getList() ) {
//			System.out.println(" msg=>" +nod.ipAddress);
			ip =  nod.ipAddress.getBytes();
			p = nod.port;
			outputBox.writeInt(ip.length);
			outputBox.write(ip);
			outputBox.writeInt(p);
		}
		listBytes = box.toByteArray();
		
		int listBytesLength = listBytes.length;
		dout.writeInt(totalCr);
		dout.writeInt(nNeededConnections);
		dout.writeInt(listBytesLength);
		dout.write(listBytes);
		dout.writeLong(payload);
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		outputBox.close();
		return marshalledBytes;
	}

	public byte[] linkWeightsMarshaller(long pyld, int nNeededConnections, MessagingNodesList mnl, int numNodes) throws IOException {
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		this.payload = pyld;
		
		ByteArrayOutputStream box=new ByteArrayOutputStream();;
		DataOutputStream outputBox=new DataOutputStream(box);
		byte[] listBytes = null, ip = null, originIP = null;
		int i=0, p=0, weight=0, originP=0;
		for(NodeLink nod: mnl.getList() ) {
			ip = nod.ipAddress.getBytes();
			p = nod.port;
			originIP = nod.contactIP.getBytes();
			originP = nod.contactPort;
			
			weight = nod.getLinkWeight();
			outputBox.writeInt(ip.length);
			outputBox.write(ip);
			outputBox.writeInt(p);
			
			outputBox.writeInt(originIP.length);
			outputBox.write(originIP);
			outputBox.writeInt(originP);
			outputBox.writeInt(weight);
		}
		listBytes = box.toByteArray();
		
		int listBytesLength = listBytes.length;
		
		dout.writeInt(nNeededConnections);
		dout.writeInt(listBytesLength);
		dout.write(listBytes);
		dout.writeInt(numNodes);
		dout.writeLong(payload);
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		outputBox.close();

		return marshalledBytes;
	}

	public byte[] taskInitiateMarshaller(long pyld, int rounds) throws IOException {
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		this.payload = pyld;

		dout.writeInt(rounds);
		dout.writeLong(payload);
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	public byte[] taskCompleteMarshaller(long pyld, String ipAddr, int portNumber) throws IOException {
		return registerMarshaller(pyld, ipAddr, portNumber);
	}

	public byte[] pullTrafficMarshaller() {
		return marshalledBytes;
	}

	public byte[] trafficSummaryMarshaller() {
		return marshalledBytes;
	}
	public byte[] forwardMarshaller(long pyld, ArrayList<String> nextSteps) throws IOException {
		this.payload = pyld;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =	new DataOutputStream(new BufferedOutputStream(baOutputStream));
		ByteArrayOutputStream box=new ByteArrayOutputStream();
		DataOutputStream outputBox=new DataOutputStream(box);

		int routeCount = nextSteps.size();
		byte[] routeBytes = null, nodeIP = null;
		int ipSize = 0;
		while(!nextSteps.isEmpty()) {
			nodeIP = nextSteps.remove(0).getBytes();
			ipSize = nodeIP.length;
			outputBox.writeInt(ipSize);
			outputBox.write(nodeIP);
		}
		routeBytes= box.toByteArray();
		int listBytesLength = routeBytes.length;
		
		
		dout.writeInt(routeCount);
		dout.writeInt(listBytesLength);
		dout.write(routeBytes);
		dout.writeLong(payload);
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
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
		else if(message.contentEquals( "FORWARD")) messageType = 10;
		else {System.out.println("message type does not exist.");}
		return messageType;
	}
	public long getPayload() {return payload;}
	public byte[] getByteArray() { return marshalledBytes; }
	public int getMessageType() { return messageType; }




}
