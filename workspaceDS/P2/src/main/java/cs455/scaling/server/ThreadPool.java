package cs455.scaling.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ThreadPool{
	
	private static BlockingQueue <WorkerThread> threadPool = new LinkedBlockingQueue<WorkerThread>();

	public static void addToPool(WorkerThread worker) {
		threadPool.add(worker);
	}
	
	public static WorkerThread getFromPool() {
		return threadPool.poll();
	}
	
}
