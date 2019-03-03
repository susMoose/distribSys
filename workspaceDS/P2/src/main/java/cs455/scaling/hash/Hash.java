package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	
	/* This code comes from Lab session 05 slides 
	 * 
	 * Note: This code won’t always return strings of the same length. 
	 * Either pad the start with 0’s to get a consistent length or send
	 * the actual length of the string first
	 */
	public String SHA1FromBytes(byte[] data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e){
			System.out.println("Could not find algorithm SHA1: " + e);
			return null;
		}
		byte[] hash = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hash);
		return hashInt.toString(16);
	}


	public static void main(String[] args) {

	}

}
