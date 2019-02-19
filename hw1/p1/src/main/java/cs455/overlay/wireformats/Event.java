package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.MessagingNodesList.NodeLink;

public class Event {
	private  byte[] marshalledBytes;
	private ByteArrayInputStream iStream;
	private DataInputStream din;
	private Node node;
	private Socket originSocket;


	public Event (byte[] data, Node n, Socket sock ) {
		this.marshalledBytes = data;
		this.node = n;
		this.originSocket = sock;
	}


	/** Message byte array should contain: 
	 * 	int ipAddressLength , Byte[] ipAddress, int portNumber, long payload
	 */
	public void readRegisterRequest(MessagingNodesList mNodeList) {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			//Reading IP Address
			int ipAddressLength = din.readInt();
			byte[] ipAddrBytes = new byte[ipAddressLength];
			din.readFully(ipAddrBytes);
			String ipAddress = new String(ipAddrBytes);

			int portNumber = din.readInt();
			long payload = din.readLong();

			System.out.println("|> register rqst from: "+ipAddress.substring(0,6) +", Payload Recieved: " + payload);

			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();

			String additionalInfo="", statusCode="";

			// DO NOT FORGET : Check if request matches the requests origin //ADD EXCEPTION MESSAGE
			// If the node is already registered 
			if(mNodeList==null) {
				String addr = originSocket.getRemoteSocketAddress().toString();
				node.list.addConnection(addr.substring(addr.length()-5), originSocket, originSocket.getInetAddress().getHostName());

				mNodeList.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mNodeList.getSize() +").";
				statusCode = "SUCCESS";
			}
			else if (mNodeList.searchFor(ipAddress,portNumber)){
				System.out.println("Registration request was unsuccessful as the node located at "+ipAddress+" ("+portNumber+") was already among the registered nodes.");
				additionalInfo ="Registration request failed. The node being added was already registered in the registry.";
				statusCode = "FAILURE";
			}
			else {
				String addr = originSocket.getRemoteSocketAddress().toString();
				node.list.addConnection(addr.substring(addr.length()-5), originSocket, originSocket.getInetAddress().getHostName());

				// Else add the Node 
				mNodeList.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mNodeList.getSize() +").";
				statusCode = "SUCCESS";
			}
			//Now sending a RegisterResponse
			Message response = new RegisterResponse(statusCode, additionalInfo);
			Socket senderSocket = new Socket(ipAddress, portNumber);
			TCPSender sendMessage = new TCPSender(senderSocket, response);
			node.addToSendSum(response.getPayload());
			node.incrementSendTracker();
			iStream.close(); din.close();
		} catch (IOException e) {
			System.out.println("Event.java:              Failed to read message. ");
		}
	}

	public void readRegisterResponse() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int statLength = din.readInt();
			byte[] status = new byte[statLength];
			din.readFully(status);
			int infoLength = din.readInt();
			byte[] info = new byte[infoLength];
			din.readFully(info);
			long payload = din.readLong();

			String stat = new String(status);

			if( stat.contentEquals("SUCCESS")) {
				String ipAddress = originSocket.getInetAddress().getHostName();
				
				String tempAddr = originSocket.getRemoteSocketAddress().toString();
				tempAddr = tempAddr.substring(tempAddr.length()-5);
				int port = Integer.parseInt(tempAddr);
				node.getCurrentMessagingNodesList().addNode(ipAddress, port, originSocket);
				node.decreaseNeededConnects();
			}
			
//			if(OverlayCreator.Cr == (node.getCurrentMessagingNodesList().getSize()-1) && OverlayCreator.Cr!=0 ) {
//				node.setPeerNumber(0);
//				System.out.println( "All connections are established. Number of connections: " + (node.getCurrentMessagingNodesList().getSize()-1));
//			}
			System.out.println("|> register response: "+ stat  +", Payload Recieved: " + payload);
			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();
		} catch (IOException e) {
			System.out.println("Event.java:              Failed to read message. ");
		}
	}

	public void readDeregisterRequest(MessagingNodesList mNodeList) {
	}

	public void readDeregisterResponse() {
	}

	//Each node stores this list of who they want to connect with in their future connections slot 
	@SuppressWarnings("unchecked")
	public void readMessagingNodesList() {
		System.out.println("\n-->Recieved messaging nodes list ");
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int nNeededConnections = din.readInt();
			int listByteLength = din.readInt();
			byte[] listBytes = new byte[listByteLength];
			din.readFully(listBytes);
			long payload = din.readLong();

			MessagingNodesList newList = new MessagingNodesList();

			//Unmarshall object of Messaging Nodes in list
			ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
			DataInputStream dis = new DataInputStream(bis);
			
			int nLength,nLPort;
			byte[] nodeLinkBytes;
			NodeLink nLink;
			String nIP;
			for (int i=0; i< nNeededConnections; i++ ) {
				nLength = dis.readInt();
				nodeLinkBytes = new byte[nLength];
				dis.readFully(nodeLinkBytes);
				nIP= new String(nodeLinkBytes);
				nLPort = dis.readInt();
				newList.addNode(nIP, nLPort);
			}

			node.setMessagingNodesList(newList,nNeededConnections);
			node.getFutureMessagingNodesList().showLinks();
			System.out.println("\n");

			//			System.out.println("|> Payload Recieved: " + payload );
			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();

		} catch (IOException e) {
			System.out.println("Event.java:              Failed to read message. "); 
		}
		//Once here the node has its list of people to connect to and its time to start trying to make friends and register on their friend lists

		// Creating a registration message. 
		for(int i=0; i<node.getFutureMessagingNodesList().getSize(); i++) {

			Message message;
			try {
				message = new RegisterMessage(node.ipAddr, node.portNum);
				String friendIP = node.getFutureMessagingNodesList().getNodeAtIndex(i).ipAddress;
				int friendPort = node.getFutureMessagingNodesList().getNodeAtIndex(i).port;
				System.out.println("|> Registering myself with " + friendIP.substring(0,6) + "("+friendPort+")");

				// Creating a socket that connects directly to the registry.
				Socket senderSocket = new Socket(friendIP, friendPort );

				// Sending message
				TCPSender sendingMessage = new TCPSender(senderSocket, message);

				node.addToSendSum(message.getPayload()); 
				node.incrementSendTracker();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void readLinkWeights() {
		System.out.println("\n-->Recieved Link weights list ");
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int nNeededConnections = din.readInt();
			int listByteLength = din.readInt();
			byte[] listBytes = new byte[listByteLength];
			din.readFully(listBytes);
			long payload = din.readLong();

			MessagingNodesList newList = new MessagingNodesList();

			//Unmarshall object of Messaging Nodes in list
			ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
			DataInputStream dis = new DataInputStream(bis);
			
			int nLength,nLPort,nLWeight;
			byte[] nodeLinkBytes;
			NodeLink nLink;
			String nIP;
			for (int i=0; i< nNeededConnections; i++ ) {
				nLength = dis.readInt();
				nodeLinkBytes = new byte[nLength];
				dis.readFully(nodeLinkBytes);
				nIP= new String(nodeLinkBytes);
				nLPort = dis.readInt();
				nLWeight = dis.readInt();
				newList.addNode(nIP, nLPort,nLWeight);
			}

			node.setMessagingNodesList(newList,nNeededConnections);
			node.getFutureMessagingNodesList().showLinks();
			System.out.println("\n");

			//			System.out.println("|> Payload Recieved: " + payload );
			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();

		} catch (IOException e) {
			System.out.println("Event.java:              Failed to read message. "); 
		}
		//Once here the node has its list of people to connect to and its time to start trying to make friends and register on their friend lists

		// Creating a registration message. 
		for(int i=0; i<node.getFutureMessagingNodesList().getSize(); i++) {

			Message message;
			try {
				message = new RegisterMessage(node.ipAddr, node.portNum);
				String friendIP = node.getFutureMessagingNodesList().getNodeAtIndex(i).ipAddress;
				int friendPort = node.getFutureMessagingNodesList().getNodeAtIndex(i).port;
				System.out.println("|> Registering myself with " + friendIP.substring(0,6) + "("+friendPort+")");

				// Creating a socket that connects directly to the registry.
				Socket senderSocket = new Socket(friendIP, friendPort );

				// Sending message
				TCPSender sendingMessage = new TCPSender(senderSocket, message);

				node.addToSendSum(message.getPayload()); 
				node.incrementSendTracker();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void readTaskInititate() {
	}

	public void readTaskComplete() {
	}

	public void readPullTrafficSummary() {
	}

	public void readTrafficSummary() {
	} 
}
