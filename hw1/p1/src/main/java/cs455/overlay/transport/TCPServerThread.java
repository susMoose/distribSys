package cs455.overlay.transport;
import java.io.*;
import java.net.*;

import cs455.overlay.node.Node;


public class TCPServerThread implements Runnable {
	private ServerSocket serverSocket;
	private Node node;

	/*	Constructor for Registry node.
	 *  creates thread to form serverSocket to listen for connections */
	public TCPServerThread(ServerSocket tcpSocket, Node n) throws IOException {
		
		this.serverSocket = tcpSocket;
		this.node = n;
	}

	public void run() {
//		System.out.println("TCPServerThread.java:    Now my serverSocket is waiting for a connection.");
		while(true) {
			try {
				// Block on accepting connections. Once it has received a connection it will return a socket for us to use.
				Socket sentSocket = serverSocket.accept();

				//If we get here we are no longer blocking, so we accepted a new connection on a new socket
				Thread receiverThread = new Thread(new TCPRecieverThread(sentSocket, node));
				receiverThread.start();
			}catch (IOException e) {
				System.out.println("TCPServerThread.java:     Server::main::accepting_connections:: " + e);
				System.exit(1);
			}
		}
	}


}


