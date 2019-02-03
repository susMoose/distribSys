package cs455.overlay.node;
import java.io.IOException;

import cs455.overlay.wireformats.Register;


public class MessagingNode{
	Node messageNode; 

	/* Constructor */
	public MessagingNode(String registryHost, int registryPort) {
		 messageNode = new Node();
		 Register registrationMessage = new Register(messageNode.ipAddr,messageNode.portNum, registryHost, registryPort);
		 
	}
	
	/* Prints the shortest paths to all other message nodes */
	public void printShortestPath() {}
	
	/* The messaging node leaves the overlay by sending a deregistration message and awaits 
	 * response before terminating its process and exiting.*/
	public void exitOverlay() {}
	
	
	public static void main(String[] args) throws IOException {
		//MessagingNode mnode = new MessagingNode("(ip address here)",4);
		//System.out.println(mNode.portNum);
	}

}
