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
		     /*

			System.out.println("Signing "+data+" "+data.length()+" with "+signAlgo +" "+hashAlgo);
			MessageDigest messageDigest = MessageDigest.getInstance(hashAlgo);
			byte[] digest = messageDigest.digest((data).getBytes());
			System.out.println("digest "+Base64.encodeToString(digest, false));
			Provider pro = new BouncyCastleProvider();
			Iterator<Entry<Object,Object>> it = pro.entrySet().iterator();
			while (it.hasNext()) {
				System.out.println("Entry "+it.next());
			}
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding", new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			cipher.update(digest);
			byte[] cipherText = cipher.doFinal();
			System.out.println("Signature: " + Base64.encodeToString(cipherText, false));
			Cipher cipher2 = Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher2.init(Cipher.ENCRYPT_MODE, privateKey);
			cipher2.update(digest);
			byte[] cipherText2 = cipher2.doFinal();
			System.out.println("Signature: " + Base64.encodeToString(cipherText2, false));
			Cipher cipher3 = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
			cipher3.init(Cipher.ENCRYPT_MODE, privateKey);
			cipher3.update(digest);
			byte[] cipherText3 = cipher3.doFinal();
			System.out.println("Signature: " + Base64.encodeToString(cipherText3, false));
			*/
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
