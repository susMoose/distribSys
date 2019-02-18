package cs455.overlay.node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.MessagingNodesList;
public class Node {
	public final String ipAddr;
	public final int portNum;
	private int nPeerMessagingNodes;
	private int sendTracker = 0, receiveTracker = 0, relayTracker = 0;
	private long sendSummation = 0, receiveSummation = 0;
	private MessagingNodesList futureConnectionsList= new MessagingNodesList();
	private MessagingNodesList currentConnectionsList= new MessagingNodesList();


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


	public void showMessagingNodesList() {
		currentConnectionsList.showLinks();
	} 
	
	public MessagingNodesList getCurrentMessagingNodesList() {
		return currentConnectionsList;
	} 
	public MessagingNodesList getFutureMessagingNodesList() {
		return futureConnectionsList;
	} 
	
	
	public void getSums() {
		System.out.println("SendSummation: "+ sendSummation +", ReceiveSummation: "+ receiveSummation + ", SendTracker: "+ sendTracker + ", RecieveTracker: "+ receiveTracker);
//		System.out.println("RelayTracker: "+ relayTracker);
	}
	public void setMessagingNodesList(MessagingNodesList mnl) {
		this.currentConnectionsList= mnl;
	}
	
	public void setMessagingNodesList(MessagingNodesList mnl, int connectionsNumber) {
		this.nPeerMessagingNodes = connectionsNumber;
		this.futureConnectionsList = mnl;
	}
	
	public int getNumberNeededConnections() {return nPeerMessagingNodes;}
}
