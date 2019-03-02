package cs455.overlay.util;


public class StatisticsCollectorAndDisplay {
	private static int nSent = 0 ;
	private static int nReceived= 0 ;
	private static long sSum= 0 ;
	private static long rSum= 0 ;
	private static int nodeNum= 0;
	
	
	public static void printTitles() {
		System.out.println("          Number_Sent   Number_Received   Sent_Message_Sum   Received_Message_Sum   Number_Relayed" );
	}
	public static void printDataLine(int numberSent, int numberReceived,  long sentSum, long reciptSum, int numberRelayed) {
		nSent +=numberSent;
		nReceived +=numberReceived;
		sSum += sentSum;
		rSum += reciptSum;
		
		System.out.printf("  Node %d     %-5d           %-6d        %-17d    %-15d  %12d\n", nodeNum, numberSent, numberReceived,sentSum, reciptSum, numberRelayed );
		nodeNum ++;
	}
	public static void printTotals() {
		System.out.printf("     Sum     %-5d           %-6d        %-17d    %-15d  \n", nSent, nReceived, sSum, rSum);

	}
	
	public static void clearData() {
		nSent = 0 ;
		nReceived= 0 ;
		sSum= 0 ;
		rSum= 0 ;
		nodeNum= 0;
	}
	
//	public static void main(String[] args) throws IOException {
//		printTitles();
//		printDataLine(25000,25440 ,-340040800604L, 192844494434L, 60770);
//		printTotals();
//	}
}
