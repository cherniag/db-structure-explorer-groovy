package mobi.nowtechnologies.server.trackrepo.uits;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crypto {

    private static final Logger logger = LoggerFactory.getLogger(Crypto.class);
    private MessageDigest hashSum;

    public Crypto(String algo) {
        try {
            hashSum = MessageDigest.getInstance(algo);
        }
        catch (NoSuchAlgorithmException e) {
            logger.error("Error : {}", e.getMessage(), e);
        }
    }

    public static byte[] sign(String data, PrivateKey privateKey) {
        // Encrypt digest
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature sig = Signature.getInstance("SHA256WITHRSAENCRYPTION");
            sig.initSign(privateKey);
            sig.update(data.getBytes());
            return sig.sign();
        }
        catch (Exception e) {
            logger.error("Error : {}", e.getMessage(), e);
        }
        return null;
    }

    public void addHash(byte[] data, int size) {
        hashSum.update(data, 0, size);
    }

    public String finalizeHash() {
        byte[] partialHash = null;
        partialHash = hashSum.digest();
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < partialHash.length; i++) {
            String hex = Integer.toHexString(0xFF & partialHash[i]);
            if (hex.length() == 1) {
                out.append('0');
            }
            out.append(hex);
        }

        return out.toString();

    }
}
