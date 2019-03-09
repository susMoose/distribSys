package cs455.scaling.server;

import java.nio.channels.SelectionKey;

public class WorkerThread implements Runnable{
	
	public WorkerThread() {	}
	

	public void run() {
		while(true) {
			synchronized(this){
				System.out.println("on a thread ");
				ThreadPool.addToPool(this);
				try {
					wait(); 
				} catch (InterruptedException e) { e.printStackTrace();	}
			}
			//perform action??		
		}
	}
	
	
	public void acceptConnection(SelectionKey ke) {
		
	}
	
}
