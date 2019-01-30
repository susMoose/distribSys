package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPRecieverThread implements Runnable {
	private Socket socket;
	private DataInputStream din;
	
	/* Constructor */
	public TCPRecieverThread(Socket socket) throws IOException {
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
	}
	
	
	public void run() {
		int dataLength;
		while (socket != null) {
			try {
				dataLength = din.readInt();
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
			} catch (SocketException se) {
				System.out.println(se.getMessage());
				break;
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage()) ;
				break;
			}
		}
	}
}