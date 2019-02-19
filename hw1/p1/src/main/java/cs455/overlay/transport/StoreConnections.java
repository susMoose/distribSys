package cs455.overlay.transport;

import java.net.Socket;
import java.util.HashMap;

public class StoreConnections {
	private HashMap<String, Socket> hmap = new HashMap<String, Socket>();
	private HashMap<String, Socket> nameMap = new HashMap<String, Socket>();
    
    public void addConnection(String key, Socket s, String name) {
    	hmap.put(key, s);
    	nameMap.put(name, s);
    }
    public HashMap getMap() {
		return hmap;
    }
//    public String getIP ( String inputPort) {
//    	return nameMap.get(inputPort);
//    }
    public Socket getSocket(String inputPort) {
    	return hmap.get(inputPort);
    }
    public Socket getSocketWithName(String name) {
    	return nameMap.get(name);
    }
    
//    hmap. 
}
