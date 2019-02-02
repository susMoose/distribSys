package cs455.overlay.node;
import java.io.IOException;

import cs455.overlay.transport.TCPServerThread;
public class Node {
	public String ipAddr;
	public int portNum;
	
	// Node constructor
	public Node(String ipAddress, int portNumber){
		ipAddr = ipAddress;
		portNum = portNumber;
		
		//may have to change later because of the assigned port numbers
		try {
			TCPServerThread gPort = new TCPServerThread();
			portNum = gPort.portFinder;		//assign port number
		} catch (IOException e) {
			e.printStackTrace();	//alter later
		}
		
	}
	
}
