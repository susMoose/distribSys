package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskBatch {
	private ConcurrentLinkedQueue<SelectionKey> batch;
	
	/** Constructor */
	public TaskBatch() {
		batch = new ConcurrentLinkedQueue<SelectionKey>();
	}
	
	/** Add a key to the batch */
	public synchronized  void addUnit(SelectionKey key) {
		batch.add(key);
	}
	
	/** Get the first SelectionKey from the batch */
	public synchronized SelectionKey poll() {
		return batch.poll();
	}
	/** Get this batch's size */
	public int size() {
		return batch.size();
	}
	
	/**Empty the batch */
	public void empty() {
//		batch = new LinkedList<SelectionKey>();
		batch.clear();
	}
	
}
