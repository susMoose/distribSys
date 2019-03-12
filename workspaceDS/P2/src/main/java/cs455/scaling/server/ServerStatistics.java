package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class ServerStatistics implements Runnable{
	private Timer timer;
	private static Integer nActive = 0;
	private static Integer nMessages = 0;
	private static ConcurrentHashMap<SelectionKey, Integer> clientMap;
	
	public ServerStatistics () {
		timer = new Timer();
		clientMap = new ConcurrentHashMap<SelectionKey, Integer>();
	}
	
	/** Increments the number of messages the sever has processed and
	 *  increments the specific client's message count. */ 
	public synchronized static void incrementNumberProcessed(SelectionKey key) {
//		synchronized (nMessages) {
			clientMap.replace(key, clientMap.get(key)+1);
			nMessages += 1;
//		}
	}
	
	public synchronized static void incrementActiveConnections(SelectionKey key) {
		clientMap.put(key,0);
		nActive += 1;
	}
	
	
	/** Prints server statistics every 20 seconds */
	class statsPrinter extends TimerTask{
		public synchronized void  run() {
			Calendar cal = Calendar.getInstance();
			System.out.printf("[" + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "] ");
			System.out.printf("Server Throughput: %.2f messages/s, ", (nMessages/20.0));		
			
			double averageThroughput = 0 ;
			for(int clientMsgNumber: clientMap.values()) {
				averageThroughput += (clientMsgNumber/20.0);
			}
			System.out.printf("Active Client Connections: %d, \n", nActive);
			
			
			nMessages = 0;
			
//			System.out.printf("Mean Per-client Throughput: p messages/s, ");
//			System.out.printf("Std. Dev. Of Per-client Throughput: q messages/s\n\n");
			
		}
	}
	
	public void run() {
		timer.scheduleAtFixedRate(new statsPrinter(),1000*5, 1000*5 );	// should execute TimerTask's run method every 20 seconds 
	}
	


}
