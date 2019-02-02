package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPServerThread  {
	public ServerSocket myServerSocket;
	public int portFinder = 1025;

	/* Constructor */
	public TCPServerThread() throws IOException {
		findSocketandPort(); 
	}	
	
	/* Creates a socket and tries to connect to a bunch of ports and finishes when it succeeds.
	 * The socket connects to the first open port it finds.Once it completes one can assume a
	 * successful connection was made. 
	 */
	public void findSocketandPort() throws IOException {
		while(true) {
			try {	
				//String SERVER_ADDRESS = "harrisburg.cs.colostate.edu";
				myServerSocket = new ServerSocket(portFinder); 
				System.out.println("connected to new port: "+ portFinder);
				break;	
			} catch(IOException e) {
				portFinder++; 
				System.out.println("Client::main::creating_the_socket:: " + e);
			}
		}
	}	
	
	public static void main(String[] args) throws IOException {
		TCPServerThread x = new TCPServerThread();
		TCPServerThread y = new TCPServerThread();
	}

}


// NOTES: 
// the line below is used if a machine has multiple ip addresses 
//ServerSocket serverSocket = new ServerSocket(5000, 100, InetAddress.getHostByName(“address2.cs.colostate.edu”));
// TCPServerSocket is used to accept incoming TCP communications. 
