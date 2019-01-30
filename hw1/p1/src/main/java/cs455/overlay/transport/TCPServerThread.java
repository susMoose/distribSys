package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPServerThread  {
	Socket socket;
	public int portFinder;
	
	/* Constructor */
	public TCPServerThread() throws IOException {
		portFinder = 1024; 
		findSocketandPort(); 
		//socket.close(); //move later
	}	
	
	/* Creates a socket and tries to connect to a bunch of ports and finishes when it succeeds.
	 * The socket connects to the first open port it finds.Once it completes one can assume a
	 * successful connection was made. 
	 */
	public int findSocketandPort() throws IOException {
		while(true) {
			try {	
				String SERVER_ADDRESS = "frankfort.cs.colostate.edu";
				socket = new Socket(SERVER_ADDRESS, portFinder); 
				return portFinder;	//Remove hard coded hostname later
			} catch(IOException e) {
				portFinder++; 	//System.out.println("Client::main::creating_the_socket:: " + e);
			}
		}
	}	
	
	
//	public static void main(String[] args) throws IOException {
//		TCPServerThread x = new TCPServerThread();
//	}

}




// NOTES: 
// the line below is used if a machine has multiple ip addresses 
//ServerSocket serverSocket = new ServerSocket(5000, 100, InetAddress.getHostByName(“address2.cs.colostate.edu”));
// TCPServerSocket is used to accept incoming TCP communications. 
