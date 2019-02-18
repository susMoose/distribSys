package cs455.overlay.node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.CommandInput;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.RegisterMessage;


public class MessagingNode{
	private Node mNode; 

	/* Constructor */
	public MessagingNode(String registryHost, int registryPort) throws IOException {
		Thread listener = new Thread (new CommandInput(this)); listener.start();

		// Creating our inner node that holds the ServerSocket
		mNode = new Node();	
		
		// Creating a registration message. 
		Message message = new RegisterMessage(mNode.ipAddr, mNode.portNum);

		// Creating a socket that connects directly to the registry.
		Socket senderSocket = new Socket(registryHost, registryPort);

		// Sending message
		TCPSender sendingMessage = new TCPSender(senderSocket, message);
		mNode.addToSendSum(message.getPayload()); 
		mNode.incrementSendTracker();
	}

	/* Prints the shortest paths to all other message nodes */
	public void printShortestPath() {}

	/* The messaging node leaves the overlay by sending a deregistration message and awaits 
	 * response before terminating its process and exiting.*/
	public void exitOverlay() {}


	public static void main(String[] args) throws IOException {
		int mPort = Integer.parseInt(args[1]);
		MessagingNode mnode = new MessagingNode(args[0],mPort);
	}

}
