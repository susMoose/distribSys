package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class TaskQueue implements Runnable {
	private ConcurrentLinkedQueue<ConcurrentLinkedQueue<SelectionKey> > taskQ;	
	private final int batchSize;
	private final long batchTime;
	private long startTime = Integer.MAX_VALUE;
	private ConcurrentLinkedQueue<SelectionKey>  currentBatch; 
	private final Semaphore semaphore;

	/** TaskQueue Constructor */
	public TaskQueue(int batch_size, double batch_time) {
		batchSize = batch_size; 
		batchTime = (long) (batch_time*1000);	//converting to milliseconds 
		taskQ = new ConcurrentLinkedQueue<ConcurrentLinkedQueue<SelectionKey> >();
		semaphore = new Semaphore(0); 			
		currentBatch = new ConcurrentLinkedQueue<SelectionKey> (); 		
	}

	/** Takes a selection key which it adds to the current batch being filled */
	public void addTask(SelectionKey key) {
		synchronized (currentBatch) {
			if(currentBatch.size() == 0 ) {
				startTime = System.currentTimeMillis();
			}
			currentBatch.add(key);

			if(currentBatch.size() == batchSize) { 		// If we reach batch size limit, put batch in TaskQueue
				addBatch();
			}
		}
	}

	/** This is where threads wait to access tasks */
	public ConcurrentLinkedQueue<SelectionKey>  waitForTasks() {
		try {	
			semaphore.acquire();  // Waits here until it can get something from queue 
		}		
		catch (InterruptedException e) { 
			e.printStackTrace(); 
		}
		return taskQ.poll();
	}

	/** This thread should constantly be checking what batch is at the front of the task queue */
	public void run() {
		while (true) {
			try {
				Thread.sleep(batchTime);
				synchronized (currentBatch) {
					if((startTime+1000*batchTime) <= System.currentTimeMillis() && (currentBatch.size()>0)) { 
						System.out.println("woke");
						addBatch();
					}
				}
			} catch (InterruptedException e) {e.printStackTrace();	}
		}
	}
	
	/** Puts the batch last touched into the task queue */
	private void addBatch() {
		if ( currentBatch.size()>0) {
			taskQ.add(currentBatch);
			semaphore.release(); 	//releases a permit so a workerThread access taskQ
			currentBatch = new ConcurrentLinkedQueue<SelectionKey> ();
		}
	}

}
