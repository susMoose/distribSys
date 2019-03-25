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
		clientMap.replace(key, clientMap.get(key)+1);
		nMessages += 1;
	}
	
	/** Increments the number of active connections the sever has and
	 *  stores the key of the newly connected channel. */ 
	public synchronized static void incrementActiveConnections(SelectionKey key) {
		clientMap.put(key,0);
		nActive += 1;
	}

	/** Prints server statistics every 20 seconds */
	class statsPrinter extends TimerTask{
		public void  run() {
			synchronized (this) {
				Calendar cal = Calendar.getInstance();
				System.out.printf("[" + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "] ");
				System.out.printf("Server Throughput: %.2f messages/s, ", nMessages/20.0);		
				System.out.printf("Active Client Connections: %d, ", nActive);
				if (nActive ==0) {
					System.out.printf("Mean Per-client Throughput: %.2f messages/s, ", 0.0);
					System.out.printf("Std. Dev. Of Per-client Throughput: %.2f messages/s\n",0.0);
				}else {
					double averageThru = 0;
					for(int clientMsgNumber: clientMap.values()) 
						averageThru += (clientMsgNumber/20);
					
					System.out.printf("Mean Per-client Throughput: %.2f messages/s, ", (averageThru/nActive));
					double std = 0.0;
					for(SelectionKey key: clientMap.keySet()){
						std += Math.pow((clientMap.get(key)/20) - (averageThru/nActive), 2);
						clientMap.replace(key, 0);
					}
					
					System.out.printf("Std. Dev. Of Per-client Throughput: %.2f messages/s\n", Math.sqrt(std/nActive));
					nMessages=0;
				}
			}
		}
	}

	public void run() {
		timer.scheduleAtFixedRate(new statsPrinter(),1000*20, 1000*20 );	// should execute TimerTask's run method every 20 seconds 
	}



}
