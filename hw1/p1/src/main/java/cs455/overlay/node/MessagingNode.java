package cs455.overlay.node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.CommandInput;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.RegisterMessage;


public class MessagingNode extends Node{
	private Node mNode; 
	private String regHost;

	/* Constructor */
	public MessagingNode(String registryHost, int registryPort) throws IOException {
		Thread listener = new Thread (new CommandInput(this)); listener.start();
		mNode = new Node();			// Creating inner node that holds the ServerSocket
		mNode.setPeerNumber(Integer.MAX_VALUE);

		mNode.startEventQThreads();	//starting worker threads

//		System.out.println("I am "+ mNode.ipAddr + "("+ mNode.portNum+")\n");

		regHost=registryHost;
//		System.out.println("Sent registration message to " + registryHost + ".");
		Message message = new RegisterMessage(mNode.ipAddr, mNode.portNum);
		Socket senderSocket = new Socket(registryHost, registryPort); // Creating a socket that connects directly to the registry.
		mNode.connectionsMap.addConnection(registryHost, senderSocket);
		
		// Sending message
		TCPSender sendingMessage = new TCPSender(senderSocket, message);
		mNode.addToSendSum(message.getPayload()); 
		mNode.incrementSendTracker();

	}
	
	/* Returns the current number of nodes that this node has connected with */
	public int getListSize() {
		return mNode.getCurrentMessagingNodesList().getSize();
	}
	
	/* Prints the shortest paths to all other message nodes */
	public void printShortestPath() {
		mNode.showPath();
	}

	/* The messaging node leaves the overlay by sending a deregistration message and awaits 
	 * response before terminating its process and exiting.*/
	public void exitOverlay() {
		try {
			Message message = new Deregister(mNode.ipAddr, mNode.portNum);
			Socket senderSocket = mNode.connectionsMap.getSocketWithName(regHost);			// Creating a socket that connects directly to the registry.
			// Sending message
			TCPSender sendingMessage = new TCPSender(senderSocket, message);
			mNode.addToSendSum(message.getPayload()); 
			mNode.incrementSendTracker();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Lists host name + port numbers */
	public void listMessagingNodes() {
		mNode.showMessagingNodesList();
//		System.out.println("Future connections");
//		mNode.getFutureMessagingNodesList().showLinks();
	}

	public void showHash() {
		Set set = mNode.connectionsMap.getMap().entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.print( mentry.getKey() + " --> ");
			System.out.println(mentry.getValue());
		}
	}

	public static void main(String[] args) throws IOException {
		int mPort = Integer.parseInt(args[1]);
		MessagingNode mnode = new MessagingNode(args[0],mPort);
	}

}
