//package cs455.overlay.transport;
//
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.net.Socket;
//
//import cs455.overlay.node.Node;
//
//public class SocketWaiter implements Runnable{
//	private Socket clientSocket;
//	private Node node;
//	private DataInputStream dataStream;
//
//	public SocketWaiter(Socket socket, Node n, DataInputStream din ) {
//		this.clientSocket = socket;
//		this.node = n;
//		this.dataStream = din;
//	}
//	
//	
//	public void run() {
//		while(true) {
//			System.out.println("LISTENING");
//			System.out.println(clientSocket);
//			try {
//				Integer msgLength = 0;
//				//Try to read an integer from our input stream. This will block if there is nothing.
//				msgLength = dataStream.readInt();
//				System.out.println("\nrecievedJunk");
//				TCPRecieverThread receiver = new TCPRecieverThread(clientSocket, node);
//				receiver.newMessages(msgLength);
//			} catch(IOException e) {
//				System.out.println("Client::main::talking_to_the_server:: " + e);
//				System.exit(1);
//			}
//		} 
//	}
//}
