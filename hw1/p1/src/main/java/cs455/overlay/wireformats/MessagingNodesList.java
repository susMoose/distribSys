package cs455.overlay.wireformats;

import java.io.Serializable;
import java.util.ArrayList;

public class MessagingNodesList implements Serializable{
	private ArrayList <NodeLink> messagingNodesList;

	public MessagingNodesList( ) {
		//All MessagingNodesLists are arraylists full of NodeLinks objects 
		messagingNodesList= new ArrayList<NodeLink>();
	}
	
	public MessagingNodesList(ArrayList<NodeLink> gotThisList) {
		messagingNodesList = gotThisList;
	}
	

	/* Class for Linked list of messaging Nodes objects */ 
	public class NodeLink implements Serializable{
		public final String ipAddress; 
		public final int port;

		/* Constructor */
		public NodeLink(String ip, int portNumber) {
			ipAddress = ip; 
			port = portNumber;
		}
	}
	
	
	/* Adds a node to linked list*/
	public void addNode(String ip, int portNumber) {
		NodeLink newNode = new NodeLink(ip,portNumber);
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
		System.out.println("\n Messaging Nodes List: ");
		for(NodeLink o : messagingNodesList){
			System.out.println("  "+o.ipAddress + ", "+o.port);
		}
		System.out.println();
	}



}
