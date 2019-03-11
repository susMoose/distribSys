package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class TaskBatch {
	private LinkedList<SelectionKey> batch;
	
	/** Constructor */
	public TaskBatch() {
		batch = new LinkedList<SelectionKey>();
	}
	
	/** Add a key to the batch */
	public void addUnit(SelectionKey key) {
		batch.add(key);
	}
	
	/** Get the first SelectionKey from the batch */
	public SelectionKey poll() {
		return batch.poll();
	}
	/** Get this batch's size */
	public int size() {
		return batch.size();
	}
}
