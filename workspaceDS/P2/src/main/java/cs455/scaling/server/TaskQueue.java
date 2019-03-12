package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class TaskQueue implements Runnable {
	private ConcurrentLinkedQueue<TaskBatch> taskQ;	
	private final int batchSize;
	private final long batchTime;
	private TaskBatch currentBatch; 
	private final Semaphore semaphore;

	/** TaskQueue Constructor */
	public TaskQueue(int batch_size, double batch_time) {
		batchSize = batch_size; 
		batchTime = (long) (batch_time*1000);	//converting to milliseconds 
		taskQ = new ConcurrentLinkedQueue<TaskBatch>();
		semaphore = new Semaphore(0); 			
		currentBatch = new TaskBatch(); 		
	}

	/** Takes a selection key which it adds to the current batch being filled */
	public void addTask(SelectionKey key) {
		synchronized(currentBatch) {		// Current batch is in progress and not yet in the taskQ structure 			
			currentBatch.addUnit(key);
			if(currentBatch.size() == batchSize) 		// If we reach batch size limit, put batch in TaskQueue
				addBatch();
				//				currentBatch.notify(); 			
		}
	}

	/** This is where threads wait to access tasks */
	public TaskBatch waitForTasks() {
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
		synchronized (currentBatch) {
//			while (true) {
//				try {
//					currentBatch.wait(batchTime);
					if(currentBatch.size() != 0 ) //if nonempty batch
						addBatch();
//				} catch (InterruptedException e) {e.printStackTrace();	}
//			}
		}
	}
	private void addBatch() {
//		synchronized(currentBatch) {
			taskQ.add(currentBatch);
			semaphore.release(); 	//releases a permit so a workerThread access taskQ
//			currentBatch.empty();
			currentBatch = new TaskBatch();
//		}
	}



}
