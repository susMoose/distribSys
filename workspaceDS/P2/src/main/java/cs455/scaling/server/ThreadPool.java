package cs455.scaling.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ThreadPool{
	private static BlockingQueue <WorkerThread> threadPool = new LinkedBlockingQueue<WorkerThread>();
	
	/** Add a WorkerThread to the ThreadPool */
	public static void addToPool(WorkerThread worker) {
		threadPool.add(worker);
	}
	
	/** Remove a WorkerThread to the ThreadPool */
	public static WorkerThread getFromPool() {
		return threadPool.poll();
	}
	
}
