package cs455.overlay.node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.dijkstra.ShortestPath.Vertex;
import cs455.overlay.transport.StoreConnections;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventQueue;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.MessagingNodesList.NodeLink;


public class Node {
	public final String ipAddr;
	public final int portNum;
	private int nPeerMessagingNodes;
	private int sendTracker = 0, receiveTracker = 0, relayTracker = 0;
	private long sendSummation = 0, receiveSummation = 0;
	private MessagingNodesList futureConnectionsList= new MessagingNodesList();
	private MessagingNodesList currentConnectionsList= new MessagingNodesList();
	private StoreConnections connectionsMap = new StoreConnections();
	private MessagingNodesList linkWeightsList;
	private EventQueue eQ ;
	private ShortestPath shortestPathCalculations;
	private ArrayList<Vertex> vertList;
	private String registryHostName;
	private int nodesCompleted = 0;

	// Node for registry constructor
	public Node(int portNumber) throws UnknownHostException{
		portNum = portNumber;
		InetAddress inetAddress = InetAddress.getLocalHost();
		ipAddr = inetAddress.getHostName()+ ".cs.colostate.edu";
		try {
			// TCPConnection allocates a serverSocket for the registry 
			TCPConnection tcpSocket = new TCPConnection(portNumber);		

			// Creating a new thread to listen for new connections
			Thread serverThread = new Thread (new TCPServerThread(tcpSocket.getMyServerSocket(),this));
			serverThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Node used for Messaging Node constructor
	public Node() throws IOException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		ipAddr = inetAddress.getHostName()+ ".cs.colostate.edu";

		// TCPConnection allocates a serverSocket for the messaging node 
		TCPConnection tcpSocket = new TCPConnection(0);	

		portNum = tcpSocket.getMyServerSocket().getLocalPort();

		// Creating a new thread to listen for new connections
		Thread serverThread = new Thread (new TCPServerThread(tcpSocket.getMyServerSocket(),this));
		serverThread.start();
	}
	
	public void startEventQThreads() {
		eQ = new EventQueue();
		Thread eventQThread = new Thread (eQ);
		eventQThread.start();
	}
	public void addToEventQueue(Event event) {
		eQ.insertEvent(event);
	}
	
	public void incrementSendTracker() {
		synchronized(this) {sendTracker++;}
	}
	public void incrementReceiveTracker() {
		synchronized(this) {receiveTracker++;}
	}
	public void incrementRelayTracker() {
		synchronized(this) {relayTracker++;}
	}
	public void addToSendSum(long payload) {
		synchronized(this) {sendSummation += payload;}
	} 
	public void addToReceiveSum(long payload) {
		synchronized(this) {receiveSummation += payload;}
	} 
	public synchronized void decreaseNeededConnects() {
		synchronized (this) {
				nPeerMessagingNodes--; 
			}
	}
	public void incrementNodesCompleted() {
		synchronized(this) {nodesCompleted++;}
	}
	public void decrementNodesCompleted() {
		synchronized(this) {nodesCompleted--;}
	}
	
	public void resetTrafficStats() {
		sendTracker = 0;
		receiveTracker = 0; 
		relayTracker = 0;
		sendSummation = 0l;
		receiveSummation = 0l;;
	}

	
	// The below methods are the setter methods 
	public void setPeerNumber(int peerNumber) {
		this.nPeerMessagingNodes = peerNumber;
	}
	public void setMessagingNodesList(MessagingNodesList mnl) {
		this.currentConnectionsList= mnl;
	}
	public void setMessagingNodesList(MessagingNodesList mnl, int connectionsNumber) {
		this.futureConnectionsList = mnl;
	}
	public void setLinkWeightsList(MessagingNodesList weights) { 
		this.linkWeightsList = weights;
	}
	public void setRegistryHostName (String reg) {
		this.registryHostName = reg;
	}

	
	
	// The below methods display information in the console
	public void showMessagingNodesList() {
		currentConnectionsList.showLinks();
	} 
	// Lists overlay link information in format->   node1_hostname:portNum node2_hostname:portnum link_weight \n */
	public void showLinkWeights() {
		System.out.println("Link Weights: ");
		ArrayList<NodeLink> weights = linkWeightsList.getList();
		for(NodeLink n : weights){
			System.out.printf("%s:%d %s:%d %d\n", n.contactIP,n.contactPort,n.ipAddress,n.port,n.getLinkWeight());
		}
		System.out.println();
	}
	public void showSums() {
		System.out.println("SendSummation: "+ sendSummation +", ReceiveSummation: "+ receiveSummation + ", SendTracker: "+ sendTracker + ", RecieveTracker: "+ receiveTracker);
		System.out.println("RelayTracker: "+ relayTracker);
	}		
	public void showPath() {
		shortestPathCalculations.showShortestPaths();
	}

	
	
	// Performs dijkstra calculation
	public void calculateRoutes(int numNodes) {
		shortestPathCalculations = new ShortestPath(linkWeightsList.getList(),this.ipAddr,this.portNum, numNodes);
		vertList = shortestPathCalculations.getVertList();
	}
	//finds the distance to the targeted sink node 
	public Stack<String> getNextNodesToReach(String vertexIP, int vertexPort) {
		return shortestPathCalculations.getShortestPath(vertexIP, vertexPort);
	}
	public Vertex getRandomNode() {
		return shortestPathCalculations.getRandomNode();
	}
	
	
	// The below methods are the getter methods
	public MessagingNodesList getCurrentMessagingNodesList() { return currentConnectionsList; } 
	public MessagingNodesList getFutureMessagingNodesList() { return futureConnectionsList; 	} 
	public synchronized int getNumberNeededPeers() { return nPeerMessagingNodes; }
	public ArrayList<Vertex> getVertList() {return vertList;}
	public String getRegistryName() {return registryHostName;}
	public int getNodesCompleted() {return nodesCompleted;}
	public StoreConnections getConnections() {return connectionsMap;}
	public int getSendTracker() {return sendTracker;}
	public int getReceiveTracker() {return receiveTracker;}
	public int getRelayTracker() {return relayTracker;}
	public long getSendSummation() {return sendSummation;}
	public long getReceiveSummation() {return receiveSummation;}
	
	

	
}

