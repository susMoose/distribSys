ReadMe
To run the assignment:
	- First untar the file and open a terminal in the location stored the untarred the files. Run the following commands..
		cd P2
		gradle build
		cd build/classes/java/main 
		java cs455.scaling.server.Server  portnum  thread-pool-size  batch-size  batch-time
	
	- Now open up a new terminal in the location you stored the untarred files and run the following commands...
		cd P2 
		./start2.sh number-of-clients-per-machine  server-port  message-rate

A few important notes:
	 - This start script assumes you will be using your local host for your server, if you would like to change that go into the start script and modify line 3 and put the host you want as your server where it says "$HOSTNAME"
	 - In the P2 directory there is a file called machine_list. This is a list of the 25 machines in the capitals lab. This is what you should modify if you want to change what machines the clients are run on. 
	 - I have also included a file in P2 called close.sh. To run this file simply type ./close.sh and it will terminate all the client processes.
	 
File Descriptions:	
	Files located in P2:
		ReadMe.txt - Contains information on how to run the program.
		start2.sh - This is the script used to start up multiple clients on other machines. The parameters to run this file are 3 integers. The first specifies how many clients you want run on each specified machine, the second represents the port number you started the server on, and the third represents the messaging rate for all the clients. One thing to note is that this file requires a file called machine_list to be located in the same folder.
		machine_list - This file is a list of the machines the start script will run clients on. If you want to use different machines simply add the names of the machines you would like to use as clients.
		close.sh - This script requires no parameters and will simply terminate all the processes started by start2.sh
			
	Package cs455.scaling.client :
		Client.java - Contains the Client class which requests a connection to the server located at the provided host name and port number. It then spawns threads starting up the receiving and sending of messages as well as the statistics printer.
		ClientStatistics.java - Prints the required clients statistics every 20 seconds.
		Hashcodes.java - Stores the hash codes of the messages that the client has sent out and not yet received a response on. 
		ReaderThread.java - Constantly waits at a buffer to receive and response hash values from the server.
		SenderThread.java - Sends a random 8 kilobyte array of bytes at the rate specified by the message rate parameter.
		
	Package cs455.scaling.hash :
		Hash.java - Contains the method that came from assignment which computes the hash value of a byte array. This base function was modified slightly so that it also pads the returned string containing the hash if it is less than 40 characters.
	
	Package cs455.scaling.server :
		Sever.java - Contains the Server class which spawns both the ThreadPoolManager and the ServerStatistics threads. 
		ServerStatistics.java - Prints the required server statistics every 20 seconds.
		TaskQueue.java - Contains the data structure of the units of work that the threads from the thread pool must perform. The thread pool threads access the data structure via a semaphore. The thread manager adds elements to this queue. It also has its own thread to keep track of batch time. 
		ThreadPool.java - Contains the data structure holding all of the worker threads as well as the methods allowing them to be moved in and out of the pool.
		ThreadPoolManager.java - Creates the task queue, the thread pool and its worker threads, and waits on incoming valid keys before putting them into the task queue to be handled by the worker threads.
		WorkerThread.java - Pulls batches of work out of the task queue and performs the required actions on those tasks. 


