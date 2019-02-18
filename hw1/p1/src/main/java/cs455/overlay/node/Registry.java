package cs455.overlay.node;

import java.io.IOException;

import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.CommandInput;



public class Registry {
	private int regPortNum;
	private  Node rNode;


	/* Constructor */
	public Registry(int rPortNumber) throws IOException {
		//Creating registry Node which creates server socket
		Thread listener = new Thread (new CommandInput(this)); listener.start();
		regPortNum = rPortNumber;
		rNode = new Node(regPortNum);		
	}


	/* Lists host name + port numbers */
	public void listMessagingNodes() {
		rNode.showMessagingNodesList();
	}


	/* Lists overlay link information in format->   node1_hostname:portNum node2_hostname:portnum link_weight \n */
	public void listWeights() {}


	/* Sets up overlay by sending messaging nodes the contact info for other messaging nodes */
	public void setupOverlay(int connectionRequirement) {
		OverlayCreator overlayCreation = new OverlayCreator(connectionRequirement, rNode.getMessagingNodesList(),rNode);
	}


	/*Sends a link_weights message to all register nodes in the overlay.
	 * This command should be called once after the setupOverlay command is finished. */
	public void sendOverlayLinkWeights() {}

	/* Results in Nodes exchanging messages within the overlay*/
	public void start(int numberOfRounds) {}



	public static void main(String[] args) throws IOException {
		int rPort = Integer.parseInt(args[0]);
		Registry registry = new Registry(rPort);
	}
}





