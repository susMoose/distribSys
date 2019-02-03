package cs455.overlay.wireformats;

import java.io.*;

public class Unmarshalling {
	private int type;
	private long timestamp;
	private String identifier;
	private int tracker;
	
	public void WireFormatWidget(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream =
		new ByteArrayInputStream(marshalledBytes);
		DataInputStream din =
		new DataInputStream(new BufferedInputStream(baInputStream));
		type = din.readInt();
		timestamp = din.readLong();
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		identifier = new String(identifierBytes);
		tracker = din.readInt();
		baInputStream.close();
		din.close();
	}
	
	public static void main(String[] args) {
	}

}
