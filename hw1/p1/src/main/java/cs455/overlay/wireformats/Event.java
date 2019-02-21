package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import cs455.overlay.dijkstra.ShortestPath.Vertex;
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
	private int messageType;


	public Event (int messagetype, byte[] data, Node n, Socket sock ) {
		this.marshalledBytes = data;
		this.node = n;
		this.originSocket = sock;
		this.messageType = messagetype;
	}
	public int getMessageType() { return messageType; }

	/** Message byte array should contain: 
	 * 	int ipAddressLength , Byte[] ipAddress, int portNumber, long payload
	 */
	public void readRegisterRequest() {
		MessagingNodesList mNodeList = node.getCurrentMessagingNodesList();
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

			System.out.println("|> Received register request from "+ipAddress.substring(0,7) +", Payload Recieved: " + payload);

			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();

			String additionalInfo="", statusCode="";
			// DO NOT FORGET : Check if request matches the requests origin //ADD EXCEPTION MESSAGE
			// If the node is already registered 
			if(mNodeList==null) {
				MessagingNodesList mnl = new MessagingNodesList();
				mnl.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mNodeList.getSize() +").";
				statusCode = "SUCCESS";
				node.setMessagingNodesList(mnl);
			}
			else if (mNodeList.searchFor(ipAddress,portNumber)){
				System.out.println("Registration request was unsuccessful as the node located at "+ipAddress+" ("+portNumber+") was already among the registered nodes.");
				additionalInfo ="Registration request failed. The node being added was already registered in the registry.";
				statusCode = "FAILURE";
			}
			else {
				// Else add the Node 
				mNodeList.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mNodeList.getSize() +").";
				statusCode = "SUCCESS";
			}
			//Now sending a RegisterResponse
			Message response = new RegisterResponse(statusCode, additionalInfo);
			Socket senderSocket = new Socket(ipAddress, portNumber);
			node.connectionsMap.addConnection(ipAddress, senderSocket);

			TCPSender sendMessage = new TCPSender(senderSocket, response);
			node.addToSendSum(response.getPayload()); node.incrementSendTracker();
			iStream.close(); din.close();
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
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
			System.out.println("|> Received registration response: "+ stat  +", Payload Recieved: " + payload);
			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			iStream.close(); din.close();
			//node.getSums();
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
		}
	}

	public void readDeregisterRequest() {
		MessagingNodesList mNodeList = node.getCurrentMessagingNodesList();

	}

	public void readDeregisterResponse() {
	}

	//Each node stores this list of who they want to connect with in their future connections slot 
	public void readMessagingNodesList() {
		System.out.println("\n---Received messaging nodes list---");
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
			System.out.println();

			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();
			//node.getSums();
			iStream.close(); din.close();
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
		}
		//Once here the node has its list of people to connect to and its time to start trying to make friends and register on their friend lists

		// Creating a registration message. 
		for(int i=0; i<node.getFutureMessagingNodesList().getSize(); i++) {
			try {
				Message message = new RegisterMessage(node.ipAddr, node.portNum);
				String friendIP = node.getFutureMessagingNodesList().getNodeAtIndex(i).ipAddress;
				int friendPort = node.getFutureMessagingNodesList().getNodeAtIndex(i).port;
				// Creating a socket that connects directly to the registry.
				Socket senderSocket = new Socket(friendIP, friendPort );
				node.connectionsMap.addConnection(friendIP, senderSocket);

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
		System.out.println("\n---Received Link weights list---");
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int nNeededConnections = din.readInt();
			int listByteLength = din.readInt();
			byte[] listBytes = new byte[listByteLength];
			din.readFully(listBytes);
			int numNodes = din.readInt();
			long payload = din.readLong();

			MessagingNodesList newList = new MessagingNodesList();

			//Unmarshall object of Messaging Nodes in list
			ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
			DataInputStream dis = new DataInputStream(bis);

			int nodeLength,port, originPort,linkWeight;
			byte[] nodeLinkBytes;
			String ip, originIP;
			for (int i=0; i< nNeededConnections; i++ ) {
				nodeLength = dis.readInt();
				nodeLinkBytes = new byte[nodeLength];
				dis.readFully(nodeLinkBytes);
				ip= new String(nodeLinkBytes);
				port = dis.readInt();

				nodeLength = dis.readInt();
				nodeLinkBytes = new byte[nodeLength];
				dis.readFully(nodeLinkBytes);
				originIP= new String(nodeLinkBytes);
				originPort= dis.readInt();
				linkWeight= dis.readInt();
				newList.addNode(originIP, originPort, ip, port,linkWeight);
			}
			node.setLinkWeightsList(newList);
			node.addToReceiveSum(payload);
			node.incrementReceiveTracker();

			node.calculateRoutes(numNodes);			// dijkstra's calculations
			//node.getSums();
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
		}
	}

	public void readTaskInititate() {
		System.out.println("---Received Task Initiate Message---");
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			//Reading IP Address
			int rounds = din.readInt();
			long payload = din.readLong();

			System.out.println("rounds = "+ rounds);
			for(int i = 0; i < rounds; i++) {
				///BEGin task
				//Select random node from list of nodes to send a message to 
				Vertex randomNode = node.getRandomNode();
				Stack<String> nxt= node.getNextNodesToReach(randomNode.ip,randomNode.port);
				String firstStop = nxt.pop();
				
				System.out.println(" ------> Sending message to "+randomNode.ip +" the first stop at : "+firstStop);
				ArrayList<String> nextSteps= new ArrayList<String>(nxt);
				Message message = new ForwardMessage(nextSteps);
				Socket senderSocket = node.connectionsMap.getSocketWithName(firstStop);
				TCPSender sendingMessage = new TCPSender(senderSocket, message);
				node.addToSendSum(message.getPayload());
				node.incrementSendTracker();
			}
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
		}
	}

	public void readTaskComplete() {
	}

	public void readPullTrafficSummary() {
	}

	public void readTrafficSummary() {
	}
	public void readForward() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int nRouteStops = din.readInt();
			int routeByteLength = din.readInt();
			byte[] routeBytes = new byte[routeByteLength];
			din.readFully(routeBytes);
			long payload = din.readLong();

			ArrayList<String> route = new ArrayList<String>();
			//Unmarshall route stack
			ByteArrayInputStream bis = new ByteArrayInputStream(routeBytes);
			DataInputStream dis = new DataInputStream(bis);
			int nameLength;
			byte[] ipNameBytes;
			String ip, originIP;
			for (int i=0; i< nRouteStops; i++ ) {
				nameLength = dis.readInt();
				ipNameBytes = new byte[nameLength];
				dis.readFully(ipNameBytes);
				ip= new String(ipNameBytes);
				route.add(ip);
			}
			if(nRouteStops == 0) {
				System.out.println("-------------->I was a sink node!!! :)");
				node.addToReceiveSum(payload);
				node.incrementReceiveTracker();
			}
			else {
				node.incrementRelayTracker();
				System.out.print("----->I relayed a message message for "+ route.get(route.size()-1));
				String nextStop = route.remove(0);
				System.out.println(" to " + nextStop);
				Message message = new ForwardMessage(route);
				if (node.connectionsMap.getSocketWithName(nextStop) == null) {
					System.out.println("printing connections mapp ...");
					
						System.out.println(node.connectionsMap.getMap().keySet());
					//Socket senderSocket = new Socket(node.getCurrentMessagingNodesList().get(originIP, portNumber)))
				}else {
					Socket senderSocket = node.connectionsMap.getSocketWithName(nextStop);
					TCPSender sendingMessage = new TCPSender(senderSocket, message);
				}
				System.out.println("nRoutStops = "+ nRouteStops);
				System.out.println(" next stop is " + nextStop + " 111");
//				System.out.println(" next stop is " + senderSocket + " 000");
			}
		} catch (IOException e) {
			System.out.println("Failed to read message. "); 
		}

	} 
}
