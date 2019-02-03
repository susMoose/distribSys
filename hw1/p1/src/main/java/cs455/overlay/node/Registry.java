package cs455.overlay.node;

import java.io.IOException;


public class Registry {
	private int regPortNum;
	private Node registryNode;
	
	/* Constructor */
	public Registry(int rPortNumber) throws IOException {
		regPortNum = rPortNumber;
		registryNode = new Node(regPortNum);		//Creating registry Node 
	}
	
	/* Lists host name + port numbers */
	public void listMessagingNodes() {}
	
	/* Lists overlay link information in format->   node1_hostname:portNum node2_hostname:portnum link_weight \n */
	public void listWeights() {}
	
	/* Sets up overlay by sending messaging nodes the contact info for other messaging nodes */
	public void setupOverlay(int numberOfConnections) {
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
