package cs455.overlay.transport;

import java.io.*;
import java.net.*;

public class TCPSender {
	private Socket socket;
	private DataOutputStream dout;

	/* Constructor */
	public TCPSender(Socket socket) throws IOException {
		this.socket = socket;
		dout = new DataOutputStream(socket.getOutputStream());
	}
	
	
	
	
	/* Sender */
	public void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}

	
}
