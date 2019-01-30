package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPServerThread {
	
	public static void main(String[] args) throws IOException {
		int portNumFinder = 1025; 
		Socket socket;
		boolean portFound = false; 
		
		while(!portFound) {
			try {
				//We create the socket AND try to connect to the address and port we are running the server on
				socket = new Socket("saint-paul.cs.colostate.edu", portNumFinder);
				portFound = true;
			} catch(IOException e) {
				portNumFinder++; 	//next time check 
				System.out.println("Client::main::creating_the_socket:: " + e);
			}
		}
		// once here we can assume that an open port was found 
		
		
		
	}

}


// the line below is used if a machine has multiple ip addresses 
//ServerSocket serverSocket = new ServerSocket(5000, 100, InetAddress.getHostByName(“address2.cs.colostate.edu”));


// TCPServerSocket is used to accept incoming TCP communications. 
