package cs455.overlay.transport;

import java.util.LinkedList;

import cs455.overlay.wireformats.MessagingNodesList;

public class StoreWeights {
	private static LinkedList <MessagingNodesList> all = new LinkedList<MessagingNodesList>(); 
	public static void addMessageNodeList(MessagingNodesList mnl) {
		all.add(mnl);
	}
	public static void printWeights() {
		/* Lists overlay link information in format->   node1_hostname:portNum node2_hostname:portnum link_weight \n */
		for(int i=0; i< all.size();i++) {
			for(int j=0; j<all.get(i).getSize(); j++) {
				System.out.print(all.get(i).getOwnerIP()+":"+all.get(i).getOwnerPort() + " ");
				System.out.print(all.get(i).getNodeAtIndex(j).ipAddress + ":"+all.get(i).getNodeAtIndex(j).port + " ");
				System.out.println(all.get(i).getNodeAtIndex(j).getLinkWeight());
			}
		}
	}


	public static MessagingNodesList getListAtIndex(int i){return all.get(i);}



}
