package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Message;


public class TCPSender {
	private Socket senderSocket;
	private DataOutputStream outputStream;
	
	public TCPSender(Socket socket, Message message) throws IOException {
		this.senderSocket = socket;
		outputStream = new DataOutputStream(senderSocket.getOutputStream());
		sendData(message);
	}
	
	// Writes the message data to the output stream and sends it through
	public void sendData(Message message) throws IOException {
		int dataLength = message.getByteArray().length;
		int mType = message.getMessageType();
		byte[] dataToSend = message.getByteArray();
		
		outputStream.writeInt(dataLength);
		outputStream.writeInt(mType);
		outputStream.write(dataToSend, 0, dataLength);
		outputStream.flush();
		System.out.println("TCPSender.java:         Sent message to: " + senderSocket.getInetAddress().getHostName()+"("+ senderSocket.getPort()+")");
	}
	public static void main(String[] args) throws IOException {

	}


}
