package mobi.nowtechnologies.java.server.uits;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {
	
	final static int BUFFER_SIZE = 16384;
	MessageDigest hashSum;

	public Crypto() {
		
	}
	
	public Crypto(String algo) {
		try {
			hashSum = MessageDigest.getInstance(algo);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetHash() {
		
	}
	
	public void addHash(byte[] data, int size) {
		hashSum.update(data, 0, size);
	}
	
	public String finalizeHash() {
        byte[] partialHash = null;
        partialHash = hashSum.digest();
        StringBuffer out = new StringBuffer();
    	for(int i = 0; i < partialHash.length; i++) {
    		String hex = Integer.toHexString(0xFF & partialHash[i]);
    		if (hex.length() == 1) {
    		    out.append('0');
    		}
    		out.append(hex);
    	}

        return out.toString();

	}
	
	public static byte[] sign(String data, String hashAlgo, String signAlgo, PrivateKey privateKey) {
		

		// Encrypt digest
		try {
		    Security.addProvider(new BouncyCastleProvider());
			Signature sig = Signature.getInstance("SHA256WITHRSAENCRYPTION");
			sig.initSign(privateKey);
			sig.update(data.getBytes());
			byte[] cipherText = sig.sign();
			//System.out.println("Signature: " + Base64.encodeToString(cipherText, false));

			return cipherText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;

	}
	
	String getHash(RandomAccessFile file, long offset, String algo) {
		try {
			MessageDigest hashSum = MessageDigest.getInstance(algo);
	        byte[] buffer = new byte[BUFFER_SIZE];
	        byte[] partialHash = null;

	        long read = 0;

	        // calculate the hash of the hole file for the test
	      //  long offset = file.length() - file.getFilePointer();
	        int unitsize;
	        while (read < offset) {
	                unitsize = (int) (((offset - read) >= BUFFER_SIZE) ? BUFFER_SIZE : (offset - read));
	                file.read(buffer, 0, unitsize);
	                hashSum.update(buffer, 0, unitsize);
	                read += unitsize;
	        }

	        partialHash = hashSum.digest();
	        StringBuffer out = new StringBuffer();
	    	for(int i = 0; i < partialHash.length; i++) {
	    		String hex = Integer.toHexString(0xFF & partialHash[i]);
	    		if (hex.length() == 1) {
	    		    out.append('0');
	    		}
	    		out.append(hex);
	    	}

	        return out.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	

}
