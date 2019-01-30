package cs455.overlay.transport;

import java.io.*;
import java.net.*;

public class TCPSender {
	private Socket socket;
	private DataOutputStream dout;

	/* Constructor */
	public TCPSender() throws IOException {
		TCPServerThread tcpSocket = new TCPServerThread();		//creates a socket with a port num
		this.socket = tcpSocket.socket;	
		dout = new DataOutputStream(socket.getOutputStream());

		socket.close();
	}
	
	
	/* Sender */
	public void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}
	
	public static void main(String[] args) throws IOException {
		TCPSender h = new TCPSender();
	}

	
}
