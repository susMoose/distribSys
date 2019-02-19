package cs455.overlay.wireformats;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class MessagingNodesList{
	private ArrayList <NodeLink> messagingNodesList;
	private String ownerNodeIP;
	private int ownerNodePort;
	

	public MessagingNodesList( ) {
		//All MessagingNodesLists are arraylists full of NodeLinks objects 
		messagingNodesList= new ArrayList<NodeLink>();
	}
	
	public MessagingNodesList(ArrayList<NodeLink> gotThisList) {
		messagingNodesList = gotThisList;
	}
	public MessagingNodesList( String ownerIP, int ownerPort) {
		//All MessagingNodesLists are arraylists full of NodeLinks objects 
		messagingNodesList= new ArrayList<NodeLink>();
		ownerNodeIP = ownerIP;
		ownerNodePort = ownerPort;
	}
	public String getOwnerIP() {return ownerNodeIP;}
	public int getOwnerPort() {return ownerNodePort;}

	/* Class for Linked list of messaging Nodes objects */ 
	public class NodeLink {
		public final String ipAddress; 
		public final int port;
		private Socket socket;
		private int linkWeight;
		
		/* Constructor */
		public NodeLink(String ip, int portNumber, Socket sockN) {
			ipAddress = ip; 
			port = portNumber;
			socket = sockN;
		}
		/* Constructor */
		public NodeLink(String ip, int portNumber) {
			ipAddress = ip; 
			port = portNumber;
		}
		
		/* Constructor */
		public NodeLink(String ip, int portNumber,int linkweight) {
			ipAddress = ip; 
			port = portNumber;
			linkWeight = linkweight;
		}
		
		public int getLinkWeight() {return linkWeight;}
		
	}
	/* Adds a node to future list*/
	public void addNode(String ip, int portNumber) {
		NodeLink newNode = new NodeLink(ip,portNumber);
		messagingNodesList.add(newNode);
	}
	/* Adds a node to future list*/
	public void addNode(String ip, int portNumber,int linkweight) {
		NodeLink newNode = new NodeLink(ip,portNumber,linkweight);
		messagingNodesList.add(newNode);
	}
	
	
	/* Adds a node to linked list*/
	public void addNode(String ip, int portNumber, Socket originSocket) {
		NodeLink newNode = new NodeLink(ip,portNumber, originSocket);
		messagingNodesList.add(newNode);
	}

	public void removeNode(String ip, int portNumber) {
		if (searchFor(ip,portNumber)) {
			for(NodeLink mnode : messagingNodesList)
				if ((mnode.port == portNumber) && (mnode.ipAddress == ip))
					messagingNodesList.remove(mnode);
		}
		else { 
			System.out.println("Error: node that is trying to be removed is not in registry.");
		}
	}

	/* Search for node with certain port and ip address */
	public boolean searchFor(String ip, int portNumber) {
		if (messagingNodesList.equals(null)) {System.out.println("NUULLLLLLLLLLLLLLLL");}
		for(NodeLink mnode : messagingNodesList){
			if ((mnode.port == portNumber) && (mnode.ipAddress == ip)){
				return true;
			}
		}
		return false;
	}

	public int getSize() {
		return messagingNodesList.size();
	}
	public NodeLink getNodeAtIndex(int index) {
		return messagingNodesList.get(index);
	}
	
	public  ArrayList<NodeLink> getList() {
		return messagingNodesList;
	}
	/* Displays registered nodes */
	public void showLinks() {
		System.out.println("Messaging Nodes List: ");
		for(NodeLink o : messagingNodesList){
			System.out.println("  "+o.ipAddress + ", "+o.port);
		}
		System.out.println();
	}



}
