package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	/** 
	 * Returns a String containing the hashcode of the byte[].
	 *  
	 * Source: This code comes in part from Lab session 05 slides.
	 * 
	 * Note: This code won’t always return strings of the same length. 
	 * Either pad the start with 0’s to get a consistent length or send
	 * the actual length of the string first
	 */
	public static String SHA1FromBytes(byte[] data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e){
			System.out.println("Could not find algorithm SHA1: " + e);
			return null;
		}
		byte[] hash = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hash);

		String hashString = "", hString = hashInt.toString(16);
		int hLength = hString.length();
		if(hLength != 40) {		//Adjusting size if not identical
			int diff = (40 - hLength);
			for(int i = 0; i<diff; i++)
				hashString += "0";
			hashString += hString;
			return hashString;
		}
		return hString;
	}

//	public static void main(String[] args) {
//		for(int i= 0 ; i < 20; i++) {
//			byte[] payload = new byte[8000];
//			new Random().nextBytes(payload); 	// Generates random bytes and places them into byte array.
//
//			String h = Hash.SHA1FromBytes(payload);
//			System.out.print(h.length() + ", ");
//
//		}
//	}

}
