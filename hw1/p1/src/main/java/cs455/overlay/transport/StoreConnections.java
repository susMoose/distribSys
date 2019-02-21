package cs455.overlay.transport;

import java.net.Socket;
import java.util.HashMap;

public class StoreConnections {
	private HashMap<String, Socket> nameMap = new HashMap<String, Socket>();
    
    public void addConnection(String name, Socket s) {
    	nameMap.put(name, s);
    }
    public HashMap getMap() {
		return nameMap;
    }

    public Socket getSocketWithName(String name) {
    	return nameMap.get(name);
    }
    
}
