package cs455.scaling.client;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;

public class ClientStatistics implements Runnable {
	private Timer timer;
	private static int nSent = 0;
	private static int nReceived = 0;
	
	public ClientStatistics () {
		timer = new Timer();
	}
	
	public synchronized static void incrementNumberSent() {
		nSent += 1;
	}
	
	public synchronized static void incrementNumberReceived() {
		nReceived += 1;
	}
	
	/** Prints server statistics every 20 seconds */
	class statsPrinter extends TimerTask{
		public void run() {
			Calendar cal = Calendar.getInstance();
			System.out.printf("[" + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "] Total Sent Count: %d, Total Received Count: %d\n", nSent, nReceived);
			nSent = 0;
			nReceived = 0;
		}
	}


	public void run() {
		timer.scheduleAtFixedRate(new statsPrinter(), 0, 1000*10 );	// should execute TimerTask's run method every 20 seconds 
	}
	
}
