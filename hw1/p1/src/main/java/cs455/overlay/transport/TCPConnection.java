package cs455.overlay.transport;

import java.io.*;
import java.net.*;

public class TCPConnection {
	private ServerSocket serverSocket;

	/* Creates a server socket and iteratively connects to the first open port. 
	 * If no errors result then one can assume a successful connection was made.
	 */
	public TCPConnection(int portNumber) throws IOException {	
		if (portNumber != 0 ) {			
			// Enters here if it is a registry node 
			try {	
				serverSocket = new ServerSocket(portNumber); 
				System.out.println("TCPConnection.java:      Registry server socket connceted at port: "+ portNumber);
			} catch(IOException e) {
				System.out.println("TCPConnection.java:      Unable to connect to provided port, "+portNumber+" because of "+ e);
				System.exit(1);
			}	
		}
		else {
			// Enters here if it is a messaging node 
			int portFinder = 1025;
			while(true) {
				try {	
					serverSocket = new ServerSocket(portFinder); 
					System.out.println("TCPConnection.java:      Message Node server socket located at port: "+ portFinder);
					break;	
				} catch(IOException e) {
					portFinder++; 
				}
			}
		}
	}

	public ServerSocket getMyServerSocket() { 	return serverSocket;	}


}
