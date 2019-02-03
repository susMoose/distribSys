package cs455.overlay.node;
import java.io.IOException;

import cs455.overlay.transport.TCPServerThread;
public class Node {
	public String ipAddr;
	public int portNum;
	private int sendTracker = 0, receiveTracker = 0, relayTracker = 0;
	private long sendSummation = 0, receiveSummation = 0;
	
	

	// Node for registry constructor
	public Node(int portNumber){
		TCPServerThread registryNode;
		portNum = portNumber;
		try {	 						
			registryNode = new TCPServerThread(portNumber);   //attempting to create registry node
		} catch (IOException e) {
			System.out.println("Was unable to create a registry node located at "+ portNumber + " because of error: " + e);
			System.exit(1);
		}
	}
	// Node used for Messaging Node constructor
	public Node() {
		TCPServerThread getPort;
		try {
			getPort = new TCPServerThread();
			portNum = getPort.portFinder;		//assign port number
		} catch (IOException e) {
			System.out.println("Was unable to create a node that connected a port");
		}
	}

}
