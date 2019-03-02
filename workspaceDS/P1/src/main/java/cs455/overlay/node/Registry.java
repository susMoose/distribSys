package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cs455.overlay.transport.StoreConnections;
import cs455.overlay.transport.StoreWeights;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.CommandInput;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.ListMessage;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.TaskInitiate;



public class Registry extends Node {
	private int regPortNum;
	private  Node rNode;

	/* Constructor */
	public Registry(int rPortNumber) throws IOException {
		//Creating registry Node which creates server socket
		Thread listener = new Thread (new CommandInput(this)); listener.start();
		regPortNum = rPortNumber;
		rNode = new Node(regPortNum);
		rNode.setPeerNumber(Integer.MAX_VALUE);
		rNode.startEventQThreads();	//starting worker threads
	}


	/* Lists host name + port numbers */
	public void listMessagingNodes() {
		rNode.showMessagingNodesList();
	}
	
	public void showHash() {
		Set set = rNode.getConnections().getMap().entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.print( mentry.getKey() + " --> ");
			System.out.println(mentry.getValue());
		}
	}
	
	
	public int getListSize() {
		return rNode.getCurrentMessagingNodesList().getSize();
	}
	
	/* Lists overlay link information in format->   node1_hostname:portNum node2_hostname:portnum link_weight \n */
	public void listWeights() {
		StoreWeights.printWeights();
	}


	/* Sets up overlay by sending messaging nodes the contact info for other messaging nodes */
	public void setupOverlay(int connectionRequirement) {
		OverlayCreator overlayCreation = new OverlayCreator(connectionRequirement, rNode.getCurrentMessagingNodesList(),rNode);
	}


	/*Sends a link_weights message to all register nodes in the overlay.
	 * This command should be called once after the setupOverlay command is finished. */
	public void sendOverlayLinkWeights() {
		for(int i= 0; i < rNode.getCurrentMessagingNodesList().getSize();i++) {
			try {
				MessagingNodesList weights = StoreWeights.getWeights();
				Message message = new LinkWeights(weights, weights.getSize(),rNode.getCurrentMessagingNodesList().getSize());
				
				Socket senderSocket = rNode.getConnections().getSocketWithName(rNode.getCurrentMessagingNodesList().getNodeAtIndex(i).ipAddress);//new Socket(links.getOwnerIP(),links.getOwnerPort());
				TCPSender sendingMessage = new TCPSender(senderSocket, message);
				rNode.addToSendSum(message.getPayload());
				rNode.incrementSendTracker();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Results in Nodes exchanging messages within the overlay*/
	public void start(int numberOfRounds) {
		try {
			for(int i= 0; i < rNode.getCurrentMessagingNodesList().getSize();i++) {
				Message message = new TaskInitiate(numberOfRounds);
				Socket senderSocket = rNode.getConnections().getSocketWithName(rNode.getCurrentMessagingNodesList().getNodeAtIndex(i).ipAddress);
				TCPSender sendingMessage = new TCPSender(senderSocket, message);
				rNode.addToSendSum(message.getPayload());
				rNode.incrementSendTracker();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void main(String[] args) throws IOException {
		//int rport = Integer.getInteger(args[0]);
		Registry registry = new Registry(2222);
	}
}





