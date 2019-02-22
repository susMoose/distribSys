package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

import cs455.overlay.dijkstra.ShortestPath.Vertex;
import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
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
	
	/* Message byte array should contain:  int ipAddressLength , Byte[] ipAddress, int portNumber, long payload*/
	public  void readRegisterRequest() {
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
			din.readLong();
			iStream.close(); din.close();
			
			String additionalInfo="", statusCode="FAILURE";
			if(!ipAddress.contentEquals( originSocket.getInetAddress().getHostName())) {
				additionalInfo ="Registration request failed. The registration request has an IP which does not match the IP of the machine it came from.";
			}
			if(mNodeList==null) {	//handles null list issue if list has not been created yet
				MessagingNodesList mnl = new MessagingNodesList();
				mnl.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mnl.getSize() +").";
				statusCode = "SUCCESS";
				node.setMessagingNodesList(mnl);
			}
			else if (mNodeList.searchFor(ipAddress,portNumber)){		//Checks if the node was already registered
				additionalInfo ="Registration request failed for "+ipAddress+". The node being added was already registered in the registry.";
			}
			else { // Else add the Node 
				mNodeList.addNode(ipAddress, portNumber,originSocket);
				additionalInfo ="Registration request successful. The number of messaging nodes currently constituting the overlay is (" + mNodeList.getSize() +").";
				statusCode = "SUCCESS";
			}
			if(statusCode.contentEquals("FAILURE")) System.out.println(additionalInfo);

			node.decreaseNeededConnects();
			if(node.getNumberNeededPeers() == 0) {
				System.out.println("All connections are established. Number of connections: "+(node.getCurrentMessagingNodesList().getSize()));
			}
			//Now sending a RegisterResponse
			Message response = new RegisterResponse(statusCode, additionalInfo);
			Socket senderSocket = new Socket(ipAddress, portNumber);
			node.getConnections().addConnection(ipAddress, senderSocket);
			new TCPSender(senderSocket, response);

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
			String stat = new String(status);
			int infoLength = din.readInt();
			byte[] info = new byte[infoLength];
			din.readFully(info); din.readLong();
			iStream.close(); din.close();

			if( stat.contentEquals("SUCCESS")) {
				String ipAddress = originSocket.getInetAddress().getHostName();
				String tempAddr = originSocket.getRemoteSocketAddress().toString();
				tempAddr = tempAddr.substring(tempAddr.length()-5);
				int port = Integer.parseInt(tempAddr);
				node.getCurrentMessagingNodesList().addNode(ipAddress, port, originSocket);
				node.decreaseNeededConnects();
			}
			if(node.getNumberNeededPeers() ==0 )  System.out.println("All connections are established. Number of connections: "+(node.getCurrentMessagingNodesList().getSize()));
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	public  void readDeregisterRequest() {
		MessagingNodesList mNodeList = node.getCurrentMessagingNodesList();
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			//Reading IP Address + port
			int ipAddressLength = din.readInt();
			byte[] ipAddrBytes = new byte[ipAddressLength];
			din.readFully(ipAddrBytes);
			String ipAddress = new String(ipAddrBytes);
			int portNumber = din.readInt();
			iStream.close(); din.close();

			String additionalInfo="", statusCode="FAILURE";
			// Check if request matches the requests origin 
			if(!ipAddress.contentEquals( originSocket.getInetAddress().getHostName())) {
				additionalInfo ="The message registration request has an IP which does not match the IP of the machine it came from.";
			}
			else if(mNodeList!=null) {
				synchronized (this) {
					if (mNodeList.searchFor(ipAddress,portNumber)) {
						statusCode = "SUCCESS";
						mNodeList.removeNode(ipAddress, portNumber);
						additionalInfo = "The number of messaging nodes now constituting the overlay is (" + mNodeList.getSize() +").";
						node.setMessagingNodesList(mNodeList);
					}
				}
			}
			if (statusCode.contentEquals("FAILURE")){
				if(additionalInfo.length() < 2) additionalInfo ="Deregistration request was unsuccessful as the node at "+ipAddress+":" + portNumber+" is not a currently registered nodes.";
				System.out.println("Details:" + additionalInfo);
			}
			//Now sending a RegisterResponse
			Message response = new DeregisterResponse(statusCode, additionalInfo);
			Socket senderSocket = new Socket(ipAddress, portNumber);
			node.getConnections().addConnection(ipAddress, senderSocket);
			new TCPSender(senderSocket, response);
			originSocket.close();
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	public void readDeregisterResponse() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int statLength = din.readInt();
			byte[] status = new byte[statLength];
			din.readFully(status);
			int infoLength = din.readInt();
			byte[] info = new byte[infoLength];
			din.readFully(info);
			String stat = new String(status);
			iStream.close(); din.close();

			if( stat.contentEquals("SUCCESS")) {
				originSocket.close();
				System.exit(0);
			}
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	//Each node stores this list of who they want to connect with in their future connections slot 
	public void readMessagingNodesList() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int totalCr = din.readInt();
			int nNeededConnections = din.readInt();
			int listByteLength = din.readInt();
			byte[] listBytes = new byte[listByteLength];
			din.readFully(listBytes);

			MessagingNodesList newList = new MessagingNodesList();
			//Unmarshall object of Messaging Nodes in list
			ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
			DataInputStream dis = new DataInputStream(bis);

			int nLength, nLPort; byte[] nodeLinkBytes; String nIP;
			for (int i=0; i< nNeededConnections; i++ ) {
				nLength = dis.readInt();
				nodeLinkBytes = new byte[nLength];
				dis.readFully(nodeLinkBytes);
				nIP= new String(nodeLinkBytes);
				nLPort = dis.readInt();
				newList.addNode(nIP, nLPort);
			}
			iStream.close(); din.close();
			
			node.setMessagingNodesList(newList,totalCr);
			node.setPeerNumber(totalCr+node.getNumberNeededPeers());
			if(node.getNumberNeededPeers() ==0 )  System.out.println("All connections are established. Number of connections: "+(node.getCurrentMessagingNodesList().getSize()));

			// Creating a registration message for each node in the messagingNodesList
			for(int i=0; i<node.getFutureMessagingNodesList().getSize(); i++) {
				Message message = new RegisterMessage(node.ipAddr, node.portNum);
				String friendIP = node.getFutureMessagingNodesList().getNodeAtIndex(i).ipAddress;
				int friendPort = node.getFutureMessagingNodesList().getNodeAtIndex(i).port;

				// Creating a socket that connects directly to the registry.
				Socket senderSocket = new Socket(friendIP, friendPort );
				node.getConnections().addConnection(friendIP, senderSocket);
				new TCPSender(senderSocket, message);
			}
		} catch (IOException e) {System.out.println("Failed to read message. "); }
	}

	public void readLinkWeights() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int nNeededConnections = din.readInt();
			int listByteLength = din.readInt();
			byte[] listBytes = new byte[listByteLength];
			din.readFully(listBytes);
			int numNodes = din.readInt();

			//Unmarshall object of Messaging Nodes in list
			ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
			DataInputStream dis = new DataInputStream(bis);

			MessagingNodesList newList = new MessagingNodesList();
			int nodeLength,port, originPort, linkWeight; 
			byte[] nodeLinkBytes; String ip, originIP;
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
			node.calculateRoutes(numNodes);			// dijkstra's calculations
			System.out.println("Link weights are received and processed. Ready to send messages");
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	/* This sends a message to a random node then if rounds equals 0 task complete is sent to the registry 
	 * otherwise it makes a new task initiate method and puts it back in the event queue with a decreased number of rounds 
	 */
	public void readTaskInititate() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int rounds = din.readInt();
			for(int i = 0; i<5; i++) {
				//Select random node from list of nodes to send a message to 
				Vertex randomNode = node.getRandomNode();
				Stack<String> nxt= node.getNextNodesToReach(randomNode.ip,randomNode.port);
				String firstStop = nxt.pop();
				ArrayList<String> nextSteps = new ArrayList<String>();
				for(String x : nxt) {
					nextSteps.add(x);
				}
		
				Message message = new ForwardMessage(nextSteps);
				Socket senderSocket = node.getConnections().getSocketWithName(firstStop);
				new TCPSender(senderSocket, message);
				node.addToSendSum(message.getPayload());
				node.incrementSendTracker();
			}
			rounds = rounds -1;
			if (rounds == 0) {				// Send task Complete message if we have finished all of our rounds 
				Message completeMessage = new TaskComplete(node.ipAddr,node.portNum);
				Socket socketReturn = node.getConnections().getSocketWithName(node.getRegistryName());
				new TCPSender (socketReturn, completeMessage);
			} else {
				//Else put a new event with a taskInitiate method into the queue
				Message rerun = new TaskInitiate(rounds);
				byte[] newData = rerun.getByteArray();
				EventFactory eventFactory = EventFactory.getInstance();
				eventFactory.insert(messageType, newData, node, originSocket);
			}
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	/*When all nodes complete their tasks send a PullTraffic message */
	public void readTaskComplete() {
		node.incrementNodesCompleted();
		if(node.getNodesCompleted() ==node.getCurrentMessagingNodesList().getSize()) {
			try {
				System.out.println("going to sleep");
				Thread.sleep(1000*15);
				for(NodeLink n : node.getCurrentMessagingNodesList().getList()) {
					Message message = new PullTrafficRequest();				
					Socket senderSocket = node.getConnections().getSocketWithName(n.ipAddress);
					new TCPSender(senderSocket, message);
				}
			} catch (IOException e) {e.printStackTrace();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	// Messaging node reads the request for traffic info, gathers and sends it back
	public void readPullTrafficSummary() {
		try {
			Message message = new TrafficSummary (node.ipAddr,node.portNum, node.getSendTracker(), node.getSendSummation(),node.getReceiveTracker(),node.getReceiveSummation(),node.getRelayTracker());
			Socket socketReturn = node.getConnections().getSocketWithName(node.getRegistryName());
			new TCPSender (socketReturn, message);
		} catch (IOException e) { e.printStackTrace();}
		node.resetTrafficStats();		//reset node statistics after sent for ease of testing multiple runs
	}

	public void readTrafficSummary() {
		iStream = new ByteArrayInputStream(marshalledBytes);	
		din = new DataInputStream(new BufferedInputStream(iStream));
		try {
			int ipAddressLength = din.readInt();
			byte[] ipAddrBytes = new byte[ipAddressLength];
			din.readFully(ipAddrBytes); din.readInt();
			int nSent = din.readInt();
			long sumSent = din.readLong();
			int nReceived = din.readInt();
			long sumReceived = din.readLong();
			int nRelayed = din.readInt();

			if(node.getNodesCompleted() == node.getCurrentMessagingNodesList().getSize() ) 	// Prints title if first traffic summary received
				StatisticsCollectorAndDisplay.printTitles();
			StatisticsCollectorAndDisplay.printDataLine(nSent, nReceived,sumSent, sumReceived,  nRelayed);
			node.decrementNodesCompleted();
			if(node.getNodesCompleted() == 0 ) {
				StatisticsCollectorAndDisplay.printTotals();
				StatisticsCollectorAndDisplay.clearData();
			} 
			
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	}

	// Reading the message being relayed through the overlay and determines whether to forward or not 
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
			ByteArrayInputStream bis = new ByteArrayInputStream(routeBytes);
			DataInputStream dis = new DataInputStream(bis);
			int nameLength; byte[] ipNameBytes; String ip;
			for (int i=0; i< nRouteStops; i++ ) {
				nameLength = dis.readInt();
				ipNameBytes = new byte[nameLength];
				dis.readFully(ipNameBytes);
				ip = new String(ipNameBytes);
				route.add(ip);
			}

			if(nRouteStops == 0) {
				node.addToReceiveSum(payload);
				node.incrementReceiveTracker();
			}
			else {
				Socket senderSocket=null; Message message=null;
				node.incrementRelayTracker();
				for(int i =0; i<route.size();i++) {
					if (node.getConnections().getSocketWithName(route.get(i))!=null) {
						String nextStop = route.remove(i);
						 message = new ForwardMessage(route,payload);
						senderSocket = node.getConnections().getSocketWithName(nextStop);
					}
				}
				new TCPSender(senderSocket, message);
			}
		} catch (IOException e) { System.out.println("Failed to read message. "); }
	} 
}
