package cs455.overlay.transport;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StoreConnections {
	private ConcurrentHashMap<String, Socket> nameMap = new ConcurrentHashMap<String, Socket>();
    
    public void addConnection(String name, Socket s) {
    	nameMap.put(name, s);
    }
    public ConcurrentHashMap<String, Socket> getMap() {
		return nameMap;
    }

    public Socket getSocketWithName(String name) {
    	return nameMap.get(name);
    }
    
}
