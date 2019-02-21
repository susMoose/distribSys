package cs455.overlay.wireformats;

import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;

public class CommandInput implements Runnable {
	private Scanner scan = new Scanner(System.in);
	private Registry reg;
	private MessagingNode mNode;

	public CommandInput(Registry r) { this.reg = r;	}
	public CommandInput(MessagingNode m) { this.mNode = m;}

	public void run() {
		while (true) {
			String request = scan.next();
			if (request.contentEquals("go")) {
				reg.listMessagingNodes();
				reg.setupOverlay(2);
				reg.listWeights();
			}
			
			else if (request.contentEquals("list-messaging-nodes")||request.contentEquals("lmn")) {
				if (reg ==null) {
					mNode.listMessagingNodes();
//					System.out.println(" - There are "+ (mNode.getListSize()-1)+ " messaging nodes.");

				}else {
					reg.listMessagingNodes();
//					System.out.println(" - There are "+ reg.getListSize() + " messaging nodes.");
				}

			}
			else if(request.contentEquals("list-weights")||request.contentEquals("lw")) {
				reg.listWeights();
			}
			else if(request.contentEquals("setup-overlay")||request.contentEquals("so")) {
				int Cr = scan.nextInt();
				reg.setupOverlay(Cr);
			}
			else if(request.contentEquals("send-overlay-link-weights")||request.contentEquals("solw")) {
				reg.sendOverlayLinkWeights();
			}
			else if(request.contentEquals("start")) {
				if(scan.hasNextInt()) {
					int numRounds = scan.nextInt();
					reg.start(numRounds);
				}else {
					System.out.println("Invalid command, start requires an integer parameter");
				}
			}
			else if(request.contentEquals("print-shortest-path")||request.contentEquals("psp")) {
				mNode.printShortestPath();
			}
			else if(request.contentEquals("exit-overlay")) {
				mNode.exitOverlay();
			}
			else {
				if(scan.hasNextLine()) {request += scan.nextLine();}
				System.out.println("Your entry of \""+ request+ "\" is an invalid command");
			}
		}
	}
}
