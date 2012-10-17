package mobi.nowtechnologies.java.server.uits;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


public class UITS {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		
		String privKeyFile = args[0];
		String inputFile  = args[1];
		String outputFile = args[2];
		String dataFile = null;
		if (args.length >= 4)
			dataFile = args[3];
		
		boolean encrypt = true;
		if (args.length >= 5) {
			if ("no".equalsIgnoreCase(args[4])) {
				encrypt = false;
			}
		}

		UitsParameters params = new UitsParameters();
		params.setCopyright("All right reserved");
		params.setDistributor("ChartsNow.mobi");
		params.setIsrc("GB7438783784");
		params.setProductId("hfdjhffdjhfdjdf");
		params.setUrl("http://chartsnow.mobi");
		params.setUser("cntest3@cn.mobi");
		try {
			System.err.println("Loading key "+privKeyFile);
			File privFile = new File(privKeyFile);
			DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
			byte[] privKeyBytes = new byte[(int) privFile.length()];
			dis.readFully(privKeyBytes);
			dis.close();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			// decode private key
			// PKCS8EncodedKeySpec privSpec = new
			// PKCS8EncodedKeySpec(Base64.decode(privKeyBytes));
			PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
			RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);
			params.setKey(privKey);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InputStream in = new FileInputStream(inputFile);
			OutputStream out = new FileOutputStream(outputFile);
			if (inputFile.endsWith(".mp3")  || inputFile.endsWith(".MP3")) {
				MP3Manager mp3Manager = new MP3Manager();
				String hash = mp3Manager.mp3GetMediaHash(inputFile);
				mp3Manager.process(in, out, params, hash);
			} else { // Assume AAC.....
				OutputStream header = new FileOutputStream(dataFile);
				MP4Manager mp4manager = new MP4Manager();
				mp4manager.process(in, out, header, params, null, encrypt);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
