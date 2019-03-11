package cs455.scaling.client;

import java.util.LinkedList;

public class Hashcodes {
	private LinkedList<String> hashList;

	/** Constructor */
	public Hashcodes () {
		hashList = new LinkedList <String>();
	}
	
	/** Add a hash to the list */
	public void add(String hash) {
		hashList.add(hash);
	}
	
	/** Check if a hash is in the list and if it is, remove it from the list */
	public boolean contains(String hash) {
		for (String storedHash : hashList) 
			if ( hash.equals(storedHash)) {
				//if the hash equals one that was sent, remove it from our list
				hashList.remove(storedHash);
				return true;
			}
		return false;
	}
	public void printEveryone() {
		for (String storedHash : hashList) {
			System.out.println( "  "+ storedHash);
		}
		System.out.println("exiting in 1 seconds");
		try {
			Thread.sleep(1000*1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(2);
	}
}
