package cs455.overlay.transport;
import java.io.*;
import java.net.*;

public class TCPServerThread  {
	public ServerSocket myServerSocket;
	public int portFinder = 1025;

	/* Constructor for when we are given a specific port number  */
	public TCPServerThread(int portNumber) throws IOException {
		openRegistryPort(portNumber); 
	}
	
	/* Constructor for when we have to find an open port  */
	public TCPServerThread() throws IOException {
		findOpenPort(); 
	}	
	
	/* Opens a the port for the registry at a given port number */
	public void openRegistryPort( int portNumber) throws IOException{			
		try {	
			myServerSocket = new ServerSocket(portNumber); 
			System.out.println("Registry socket located at port: "+ portNumber);
		} catch(IOException e) {
			System.out.println("Unable to connect to provided port, "+portNumber+" because of  "+ e);
			System.exit(1);
		}
	}
	
	/* Creates a socket and tries to connect to first open port. If no errors result then one can assume a
	 * successful connection was made. */
	private void findOpenPort() throws IOException {
		while(true) {
			try {	
				myServerSocket = new ServerSocket(portFinder); 
				System.out.println("connected to new port: "+ portFinder);
				break;	
			} catch(IOException e) {
				portFinder++; 
				System.out.println("Client::main::creating_the_socket:: " + e);
			}
		}
	}	
	
	
	
//	public static void main(String[] args) throws IOException {
//		TCPServerThread x = new TCPServerThread();
//		TCPServerThread y = new TCPServerThread();
//	}

}


// NOTES: 
// the line below is used if a machine has multiple ip addresses 
//ServerSocket serverSocket = new ServerSocket(5000, 100, InetAddress.getHostByName(“address2.cs.colostate.edu”));
// TCPServerSocket is used to accept incoming TCP communications. 
