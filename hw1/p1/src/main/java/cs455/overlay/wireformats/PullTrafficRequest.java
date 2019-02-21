package cs455.overlay.wireformats;

public class PullTrafficRequest extends Message{
	private int messageType;
	private long payload;
	private byte[] messageBytes;
	
	public void PullTrafficRequest () {}
}
