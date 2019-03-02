package cs455.overlay.wireformats;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventQueue implements Runnable  {
	private Queue<Event> eventWaitingRoom;
	private final EventFactory eventFactory = EventFactory.getInstance();

	public EventQueue() {
		eventWaitingRoom = new ConcurrentLinkedQueue<Event>();
	}
	public void insertEvent( Event event) {
		eventWaitingRoom.add(event);
	}
	public Event getEvent() {
		return eventWaitingRoom.poll();
	}

	public void run() {
		while(eventWaitingRoom != null) {
			if(!eventWaitingRoom.isEmpty()) 
				eventFactory.handleEvent(getEvent());
		}
	}
}
