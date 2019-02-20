package cs455.overlay.transport;

import java.util.ArrayList;
import java.util.LinkedList;

import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.MessagingNodesList.NodeLink;

public class StoreWeights {
	static MessagingNodesList allWeights = new MessagingNodesList();
	

	public static void addMessageNodeList(String originIP, int originPort, String friendIP, int friendPort, int linkWeight){
		allWeights.addNode(originIP, originPort, friendIP, friendPort, linkWeight);
	}
	
	
	public static  void printWeights() {
		System.out.println("Link Weights: ");

		ArrayList<NodeLink> weights = allWeights.getList();
		for(NodeLink n : weights){
			System.out.printf("%s:%d %s:%d %d\n", n.contactIP,n.contactPort,n.ipAddress,n.port,n.getLinkWeight());
		}
		System.out.println();
	}


	public static MessagingNodesList getWeights(){return allWeights;}



}
