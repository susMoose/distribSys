package cs455.overlay.wireformats;

import java.io.IOException;
public class TaskComplete extends Message{
		private int messageType;
		private long payload;
		private byte[] messageBytes;
		
		public TaskComplete(String ipAddr, int portNumber) throws IOException  {
			this.messageType = getMessageNumber("TASK_COMPLETE");
			this.payload = generateRandomPayload();	
			this.messageBytes = taskCompleteMarshaller(payload, ipAddr, portNumber);		
		}

}
