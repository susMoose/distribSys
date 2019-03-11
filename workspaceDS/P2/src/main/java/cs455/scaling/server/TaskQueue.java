package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class TaskQueue implements Runnable {
	private ConcurrentLinkedQueue<TaskBatch> taskQ;	
	private final int batchSize;
	private int currentBatchSize = 0;
	private final long batchTime;
	private long startBatchTime;
	private Thread timerThread;
	private TaskBatch currentBatch; 
	private boolean timerWorking;
	private final Semaphore semaphore;
	
	
	/** TaskQueue Constructor */
	public TaskQueue(int batch_size, double batch_time) {
		System.out.println("creating taskQueue");
		batchSize = batch_size; 
		batchTime = (long) (batch_time*1000);	//converting to milliseconds 
		taskQ = new ConcurrentLinkedQueue<TaskBatch>();
		semaphore = new Semaphore(0); 			// initializing #permits to 0 so no thread accesses the empty queue
		currentBatch = new TaskBatch(); 		
	}

	/** Takes a selection key which it adds to the current batch being filled */
	public void addTask(SelectionKey key) {
		synchronized(currentBatch) {		// Current batch is in progress and not yet in the taskQ structure 
			
			if(currentBatchSize == 0 ) { 	// Starting batch time tracker thread
				timerThread = new Thread(this);	
				startBatchTime = System.currentTimeMillis(); 
				timerWorking = true;
//				System.out.println( "\t starting timer");
				timerThread.start();
			}			
			currentBatch.addUnit(key);
			currentBatchSize ++;	

			// If we have reached batch size limit, put batch in TaskQueue
			if(currentBatchSize == batchSize) {
				taskQ.add(currentBatch);
				semaphore.release(); 	//releases a permit so a workerThread access taskQ
				currentBatchSize = 0;
				currentBatch = new TaskBatch();		// clearing currentBatch 				
			}
		}
	}
	
	/** This is where threads wait to access tasks */
	public TaskBatch waitForTasks() {
		try {	semaphore.acquire();  }		//waits until it can get something from queue 
		catch (InterruptedException e) { e.printStackTrace(); }
		return taskQ.poll();
	}

	/** This thread should constantly be checking what batch is at the front of the task queue */
	public void run() {
		while (timerWorking) {
			// if batch time elapses 
			if ( (System.currentTimeMillis() - startBatchTime ) >= batchTime) {
				synchronized(currentBatch) {
//					System.out.println("\t timer stopped");
					taskQ.add(currentBatch);
					semaphore.release(); 	//releases a permit so a workerThread access taskQ
	
					currentBatchSize = 0;
					currentBatch = new TaskBatch();		// clearing currentBatch
					timerWorking = false;
				}
//				System.out.println("   out of synchronized section");
			}
		}
	}
	
	
	
	
}
