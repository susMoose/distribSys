//package cs455.overlay.wireformats;
//
//import java.util.LinkedList;
//
//public interface Protocol {
//	/* Before they arrive here all messages contain:  (removed prior to event arrival) 
//	 * 		- int length of total message
//	 * 		- int message type
//	 * 		- byte[] data  <-- this is what we filter here
//	 * 
//	 * 	Each message type has a different message layout within data
//	 */
//	
//	public LinkedList readRegisterRequest();
//	public void readRegisterResponse();
//	public void readDeregisterRequest();
//	public void readDeregisterResponse();
//	public void readMessagingNodesList();
//	public void readLinkWeights();
//	public void readTaskInititate();
//	public void readTaskComplete();
//	public void readPullTrafficSummary();
//	public void readTrafficSummary();
//	
//
//
//}
//msg protocol : the below is the message contents in order
//- int length of total message
//- int message Type
//	- byte[] containing { inner Message ends up being format: int ipAddress length,  byte[] ipAddress, int portNumber, long payload}
