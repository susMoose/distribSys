package cs455.overlay.transport;

import java.io.*;
import java.net.*;

public class TCPSender {
	private ServerSocket mySocket;
	private DataOutputStream dout;

	/* Constructor */
	public TCPSender() throws IOException {
		TCPServerThread tcpSocket = new TCPServerThread();		//creates a socket with a port num
		this.mySocket = tcpSocket.myServerSocket;	
		//dout = new DataOutputStream(mySocket.getOutputStream());
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
		TCPSender g = new TCPSender();
		
	}

	
}
