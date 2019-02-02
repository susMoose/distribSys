package cs455.overlay.node;

import java.io.IOException;

public class Registry extends Node{
	
	/* Constructor */
	public Registry(String ipAddress, int portNumber) {
		super(ipAddress, portNumber);
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
		Node rNode = new Registry("(ip address here)",4);
		Node rNode2 = new Registry("(ip address here)",4);
		
		System.out.println(rNode.ipAddr);
		System.out.println(rNode2.ipAddr);
		
	}
}
