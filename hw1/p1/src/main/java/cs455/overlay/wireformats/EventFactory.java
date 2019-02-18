package cs455.overlay.wireformats;

import cs455.overlay.node.Node;

public class EventFactory {
	private static final EventFactory instance = new EventFactory(); 
	private MessagingNodesList messagingNodesList = new MessagingNodesList();
	private EventFactory() {}

	//static method to create instance of singleton class
	public static synchronized EventFactory getInstance() {
		return instance;
	}
	
	public void createEvent(int messageType, byte[] data, Node node) {
		Event event = new Event(data, node);
		// Find what type of message was sent and call the proper unmarshalling fn
		switch(messageType) {
			case 0: event.readRegisterRequest(messagingNodesList); break;
			case 1: event.readRegisterResponse(); break;
			case 2: event.readDeregisterRequest(messagingNodesList); break;
			case 3: event.readDeregisterResponse(); break;
			case 4: event.readMessagingNodesList(); break;
			case 5: event.readLinkWeights(); break;
			case 6: event.readTaskInititate(); break;
			case 7: event.readTaskComplete(); break;
			case 8: event.readPullTrafficSummary(); break;
			case 9: event.readTrafficSummary(); break;
			default: 
				System.out.println("Event Factory.java:        The recieved message type is not one that we have listed");
				System.exit(1);
		}
		node.setMessagingNodesList(messagingNodesList);
	}
}	


