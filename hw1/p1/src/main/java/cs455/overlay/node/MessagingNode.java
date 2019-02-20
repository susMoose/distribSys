package cs455.overlay.node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.CommandInput;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.RegisterMessage;


public class MessagingNode extends Node{
	private Node mNode; 

	/* Constructor */
	public MessagingNode(String registryHost, int registryPort) throws IOException {
		Thread listener = new Thread (new CommandInput(this)); listener.start();

		mNode = new Node();			// Creating inner node that holds the ServerSocket
		mNode.startEventQThreads();	//starting worker threads
		
		System.out.println("I am "+ mNode.ipAddr + "("+ mNode.portNum+")\n");

		// Creating a registration message. 
		System.out.println("|> Registering myself with " + registryHost.substring(0,6));

		Message message = new RegisterMessage(mNode.ipAddr, mNode.portNum);

		// Creating a socket that connects directly to the registry.
		Socket senderSocket = new Socket(registryHost, registryPort);

		// Sending message
		TCPSender sendingMessage = new TCPSender(senderSocket, message);
		mNode.addToSendSum(message.getPayload()); 
		mNode.incrementSendTracker();
		
	}
	public int getListSize() {
		return mNode.getCurrentMessagingNodesList().getSize();
	}
	/* Prints the shortest paths to all other message nodes */
	public void printShortestPath() {
		mNode.showPath();
	}
	

	/* The messaging node leaves the overlay by sending a deregistration message and awaits 
	 * response before terminating its process and exiting.*/
	public void exitOverlay() {}
	
	
	
	/* Lists host name + port numbers */
	public void listMessagingNodes() {

		System.out.println("(Connections)");
		mNode.showMessagingNodesList();

		System.out.println("(Desired connections)");
		mNode.getFutureMessagingNodesList().showLinks();
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
