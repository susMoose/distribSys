package cs455.scaling.server;

import java.io.IOException;

public class Server {
	private ThreadPoolManager manager;
	/* Server Constructor */
	public Server(int port_number, int thread_pool_size, int batch_size, double batch_time) throws IOException {
			//The manager creates the threads to be put in the thread pool 
			manager = new ThreadPoolManager(port_number, thread_pool_size, batch_size, batch_time);
			Thread managementThread = new Thread(manager);
			managementThread.start();
	}

	
	
	
	public static void main(String[] args) throws IOException {
		int portNum = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		double batchTime = Double.parseDouble(args[3]);
		new Server(portNum, threadPoolSize, batchSize, batchTime);
	
//		try { new Server(3232,3,2,1);
//		} catch (IOException e) { e.printStackTrace(); }
		
	}

}
