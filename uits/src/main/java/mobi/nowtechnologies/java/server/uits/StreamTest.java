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


public class StreamTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		UitsParameters params = new UitsParameters();
		params.setCopyright("All right reserved");
		params.setDistributor("ChartsNow.mobi");
		params.setIsrc("GB7438783784");
		params.setProductId("hfdjhffdjhfdjdf");
		params.setUrl("http://musicqubed.com");
		params.setUser("64365474365746574643276473267");
		try {
			String privKeyFile = args[2];
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
			InputStream in = new FileInputStream(args[0]);
			OutputStream out = new FileOutputStream(args[1]);
			if (args[0].endsWith(".mp3")  || args[0].endsWith(".MP3")) {
				MP3Manager mp3Manager = new MP3Manager();
				String hash = mp3Manager.mp3GetMediaHash(args[0]);
				mp3Manager.process(in, out, params, hash);
			} else { // Assume AAC.....
				MP4ManagerIntf mp4manager = new MP4Manager();
				String hash = mp4manager.getMediaHash(new FileInputStream(args[3]));
				mp4manager.processHeader(in, out, params, hash);
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
