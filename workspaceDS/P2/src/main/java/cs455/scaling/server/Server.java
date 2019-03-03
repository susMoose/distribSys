package cs455.scaling.server;

import java.io.IOException;

public class Server {
	private ThreadPoolManager manager;
	/* Server Constructor */
	public Server(int port_number, int thread_pool_size, int batch_size, long batch_time) throws IOException {
		
			manager = new ThreadPoolManager(port_number, thread_pool_size);
		
	}

	
	
	
	public static void main(String[] args) {
//		int portNum = Integer.parseInt(args[0]);
//		int threadPoolSize = Integer.parseInt(args[1]);
//		int batchSize = Integer.parseInt(args[2]);
//		long batchTime = Long.parseLong(args[3]);
//		new Server(portNum, threadPoolSize, batchSize, batchTime);
	
		try { new Server(3222,2,2,1);
		} catch (IOException e) { e.printStackTrace(); }
		
	}

}
