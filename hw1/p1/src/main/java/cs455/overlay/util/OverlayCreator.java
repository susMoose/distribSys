package cs455.overlay.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.node.Node;
import cs455.overlay.transport.StoreWeights;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.ListMessage;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.RegisterMessage;

public class OverlayCreator {
	private MessagingNodesList registryMasterList;
	private MessagingNodesList peerList;
	private MessagingNodesList peerListWithLinks;
	public static int Cr ;
	private int numberOfNodes; 
	private Node registry;
	public  ArrayList<ArrayList<Integer>> contactList;	// the node in my registry master list at index i,
														// stores the indexes of the nodes they need to 
														// contact listed at their index in this array 
	

	public OverlayCreator(int connectionRequirement, MessagingNodesList nodeList, Node rNode) {
		this.Cr = connectionRequirement;
		this.registryMasterList = nodeList;
		this.numberOfNodes = registryMasterList.getSize();
		this.registry = rNode;
		// If there are too few nodes to meet connection requirement / or there are odd number of nodes and odd connection requirement
		if (numberOfNodes<=Cr || (numberOfNodes%2 != 0 && Cr%2 != 0)) {
			System.out.println("The number of nodes ("+numberOfNodes+ ") makes it impossible to form a graph with the specified connection requirement ("+Cr+")");
			return;
		}
		contactList= new ArrayList<ArrayList<Integer>>(numberOfNodes);
		//Initializing 
		for (int i=0; i< numberOfNodes; i++) {
			contactList.add(new ArrayList<Integer>());
		}
		

		
		decideMapping();
	}

	// Determines what contacting formula to use
	private void decideMapping() {
		// Cr is even
		if(Cr%2 == 0) {
			for(int i = 0; i<numberOfNodes; i++) {
				System.out.println("Node " + registryMasterList.getNodeAtIndex(i).ipAddress + ": ");
				peerListWithLinks = new MessagingNodesList(registryMasterList.getNodeAtIndex(i).ipAddress, registryMasterList.getNodeAtIndex(i).port);
				makeEvenFriends(i);
			}
		}
		// There is an even # of nodes and odd connection requirement
		else if(numberOfNodes%2 == 0 && Cr%2 != 0 ) {
			for(int i = 0; i<numberOfNodes; i++) {
				System.out.println("Node " + registryMasterList.getNodeAtIndex(i).ipAddress + ": ");
				peerListWithLinks = new MessagingNodesList(registryMasterList.getNodeAtIndex(i).ipAddress, registryMasterList.getNodeAtIndex(i).port);

				makeOddFriends(i);
			}
		}		
	}


	private void makeEvenFriends(int mainNode) {
		peerList = new MessagingNodesList();

		int  fIndex;
		for(int i=1; i==(Cr/2); i++) {
			// befriend node at index, my node + i
			fIndex = mainNode+i;
			if(mainNode+i >= numberOfNodes)  fIndex =  ((mainNode+i) % numberOfNodes);


			if (!contactList.get(mainNode).contains(fIndex)) {
				contactList.get(mainNode).add(fIndex);
				contactList.get(fIndex).add(mainNode);
				addFriend(mainNode, fIndex);
			}
			if(mainNode-i < 0) {
				// befriend node at index, my node - i (handling array index looping)
				fIndex = numberOfNodes + mainNode-i;
			}else {
				// befriend node at index, my node - i 
				fIndex =  mainNode-i;
			}
			if (!contactList.get(mainNode).contains(fIndex)) {
				contactList.get(mainNode).add(fIndex);
				contactList.get(fIndex).add(mainNode);
				addFriend( mainNode, fIndex);
			}	
		}
		StoreWeights.addMessageNodeList(peerListWithLinks);
		sendOutLists(mainNode);
	}


	private void makeOddFriends(int mainNode) {
		peerList = new MessagingNodesList();

		//Getting node directly halfway across array from our index
		int halfway = mainNode+(numberOfNodes/2);
		if (halfway > numberOfNodes-1) 	halfway = halfway%numberOfNodes;

		if (!contactList.get(mainNode).contains(halfway)) {
			contactList.get(mainNode).add(halfway);
			contactList.get(halfway).add(mainNode);
			addFriend( mainNode, halfway);
		}

		//Now getting the nodes to the left and right of halfway node
		int fIndex;
		for(int i=1; i==(Cr/2); i++) {
			fIndex = halfway+i;
			if(halfway+i >= numberOfNodes) fIndex =  ((halfway+i) % numberOfNodes);

			if (!contactList.get(mainNode).contains(fIndex)) {
				contactList.get(mainNode).add(fIndex);
				contactList.get(fIndex).add(mainNode);
				addFriend(mainNode, fIndex);
			}

			fIndex =  halfway-i;
			if(halfway-i < 0) fIndex = numberOfNodes + halfway-i;

			if (!contactList.get(mainNode).contains(fIndex)) {
				contactList.get(mainNode).add(fIndex);
				contactList.get(fIndex).add(mainNode);
				addFriend( mainNode, fIndex);
			}
		}
		StoreWeights.addMessageNodeList(peerListWithLinks);
		sendOutLists(mainNode);
	}

	private void addFriend(int main, int fIndex) {
		String friendIP = registryMasterList.getNodeAtIndex(fIndex).ipAddress;
		int friendPort= registryMasterList.getNodeAtIndex(fIndex).port;
		peerList.addNode( friendIP, friendPort);
		
		int linkWeight = new Random().nextInt((10 - 1) + 1) + 1;
		peerListWithLinks.addNode(friendIP, friendPort,linkWeight);
		
	}



	///send message from our RegNode to others
	private void sendOutLists(int reciever) {
		try {
			Message message = new  ListMessage(peerList, peerList.getSize());
			String rNodeIP  = registryMasterList.getNodeAtIndex(reciever).ipAddress;
			int rPort = registryMasterList.getNodeAtIndex(reciever).port;
			
			Socket senderSocket = new Socket(rNodeIP, rPort);
			TCPSender sendingMessage = new TCPSender(senderSocket, message);
			registry.addToSendSum(message.getPayload());
			registry.incrementSendTracker();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}
