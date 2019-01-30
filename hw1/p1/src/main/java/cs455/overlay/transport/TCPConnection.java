package cs455.overlay.transport;

import java.io.IOException;

public class TCPConnection {
	public static void main(String[] args) throws IOException {
		char c0 = '$';
		char c1 = 'W';
		byte b0 = 15;
		byte b1 = 4;
		short s0 = 5577;
		short s1 = 1234;
		int i0 = 12345;
		int i1 = -99999;
		long l0 = 8000000000L;
		long l1 = -7000000000l;
		float f0 = 1.2345f;
		float f1 = 66.7788f;
		double d0 = .00001;
		double d1 = 83475.29837;

		System.out.println(b0 +b1);
		System.out.println((b0 + b1) / 4);
		System.out.println((b0 + b1) / 4.0);
		System.out.println(s0 / 1000);
		System.out.println(s0 / 1000.0);
		System.out.println(s1 % 100);
		System.out.println((i0 - 2345) * 10);
		System.out.println(i0 - 2345 * 10);
		System.out.println(l1 + i1);
		System.out.println((f0 + f1) / (d0 * d1));
		System.out.println(0.1 + 0.2 - 0.3);
		System.out.println(6 % 4 + 12 - 3 * (8 + 3) / 2);
	
	}
	
}
