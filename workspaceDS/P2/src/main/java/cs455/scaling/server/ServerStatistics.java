package cs455.scaling.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class ServerStatistics implements Runnable{
	private Timer timer;
	private static int nActive = 0;
	private static int nMessages = 0;
	
	public ServerStatistics () {
		timer = new Timer();
	}
	
	public synchronized static void incrementNumberProcessed() {
		nMessages += 1;
	}
	
	public synchronized static void incrementActiveConnections() {
		nActive += 1;
	}

	
	/** Prints server statistics every 20 seconds */
	class statsPrinter extends TimerTask{
		public void run() {
			Calendar cal = Calendar.getInstance();
			System.out.printf("[" + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "] ");
			System.out.printf("Server Throughput: %.2f messages/s, ", (nMessages/20.0));		
			nMessages = 0;
			System.out.printf("Active Client Connections: %d, \n", nActive);
//			System.out.printf("Mean Per-client Throughput: p messages/s, ");
//			System.out.printf("Std. Dev. Of Per-client Throughput: q messages/s\n\n");
			
		}
	}
	
	public void run() {
		timer.scheduleAtFixedRate(new statsPrinter(),50, 1000*10 );	// should execute TimerTask's run method every 20 seconds 
	}
	


}
