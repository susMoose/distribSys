package cs455.scaling.client;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;

public class ClientStatistics implements Runnable {
	private Timer timer;
	private static Integer nSent;
	private static Integer nReceived;

	public ClientStatistics () {
		timer = new Timer();
		nSent = 0;
		nReceived = 0;
	}

	public static void incrementNumberSent() {
		synchronized (nSent) {
			nSent += 1;
		}
	}

	public static void incrementNumberReceived() {
		synchronized (nReceived) {
			nReceived += 1;
		}
	}

	/** Prints server statistics every 20 seconds */
	class statsPrinter extends TimerTask{
		public void run() {
			Calendar cal = Calendar.getInstance();
			synchronized(nSent) {
				synchronized(nReceived) {
					System.out.printf("[" + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "] Total Sent Count: %d, Total Received Count: %d\n", nSent, nReceived);
					nSent = 0;
					nReceived = 0;
				}
			}
		}
	}


	public void run() {
		timer.scheduleAtFixedRate(new statsPrinter(), 1000*10, 1000*10 );	// should execute TimerTask's run method every 20 seconds 
	}

}
