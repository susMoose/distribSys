Read Me
To run, after untaring the file do the following:
	cd p1 
	gradle build
	cd build/classes/java/main 


Once in the main folder:
	- to start the registry node type:
			java cs455.overlay.node.Registry port  (This needs to be done before starting the messaging nodes)
	
	- to start the messaging nodes, open a new terminal in the same directory location and run:
			java cs455.overlay.node.MessagingNode registry-host registry-port
	
The registry commands:
	These should be then entered in the following order: 
		setup-overlay number-of-connections 
		send-overlay-link-weights
		list-weights
		start number-of-rounds
	note: list-messaging-nodes can be run at any time 
			
The messaging node commands:
	exit-overlay  		  <-this command can be run at any time before the setup-overlay command is run in the registry terminal
	print-shortest-path   <-this command can be run any time after the send-overlay-link-weights command is run 
		
Dijkstra Package : 
	ShortestPath.java is a class that computes Dijkstra’s shortest path for each messaging node to reach every other node after they receive the ListWeights message. This class takes that list of connections and weights and turns it into lists of vertices and edges with which it calculates the shortest route for relaying messages.

Node Package : 
	Node.java is the super class that both Registry.java and MessagingNode.java extend from. This class is full of methods that would be common to nodes. It also contains many of the objects that the nodes access in order to perform their tasks and save important information.  
	MessagingNode.java	controls functionalities unique to the messaging nodes. This class extends the Node.java class. 	 This class is one of two that has a main method. 
	Registry.java controls the functionalities unique to the registry node and is the first class to be run when starting this program. This class is the only other class to have a main method. 

Transport Package : 
	StoreConnections.java stores a hashmap containing node Ip addresses and their corresponding sockets.
	StoreWeights.java contains the weights of edges between nodes.
	TCPConnection.java is used to creates a server socket. 
	TCPRecieverThread.java this class is spawned in a new thread by the TCPServerThread.java and  waits for data to be sent across already connected sockets. It takes any received data and sends it to the event queue. 
	TCPSender.java sends a message via a socket. 
	TCPServerThread.java this class is run by a new thread spawned by a node. This receives a node’s ServerSocket and  continuously waits for a connection. Once connected this this method accepts the connection, assigns it to a new socket and spawns a TCPReceiver thread.
		
Util Package : 
	OverlayCreator.java’s methods determines what messaging lists each node will get in order to avoid partitions. 
	StatisticsCollectorAndDisplay.java is responsible for displaying the data received from the nodes after their tasks are complete. 

Wireformats Package :
	CommandInput.java runs a thread that waits for terminal input and handles it.
	Deregister.java extends the Message.java class and is the message signaling a messaging node’s desire to leave the overlay. 
	DeregisterResponse.java extends the Message.java class and this is the message a messaging node receives to determine if it is allowed to leave the overlay. This message specifies the success or failure of the previous request and acts accordingly. 
	Event.java handles all unmarshalling of a message’s byte[] that wasn’t done in TCPreceiver.java and then performs the required action that a particular message type needs. This is the object type inside the EventQueue.
	EventFactory.java is a singleton instance that determines what gets done with received messages in the eventQue. 
	EventQueue.java implements runnable and it has a thread which takes events out of the event Queue and handles their required actions before moving on to the next thread.
	ForwardMessage.java	 extends the Message.java class and is the basic message that all nodes are forwarding back and forth after they receive the task initiate message. 
	LinkWeights.java extends the Message.java class and contains a messaging nodes list made up of NodeLink objects that contains 2 node names and the edge weight between them .
	ListMessage.java extends the Message.java class and is the message in which all nodes receive their MessagingNodesList. 
	Message.java	contains the methods shared by all messages and  performs the marshalling of each message depending on the message type. 
	MessagingNodesList.java  is a class that creates an object that allows for nodes to keep track of each other. CommandInput.java
	RegisterMessage.java extends the Message.java class and contains a message requesting to be registered in the other node’s peer messaging nodes list. 
	RegisterResponse.java extends the Message.java class returns the status of the registration request after being processed. 
	TaskComplete.java extends the Message.java class and is sent out by the messaging nodes to the registry to signal that they have finished their rounds of messages.
	TaskInitiate.java extends the Message.java class and contains the number of rounds that the nodes will send messages for.
	TrafficSummary.java prints out the final output describing the sending, receiving, and relaying of messages.
		