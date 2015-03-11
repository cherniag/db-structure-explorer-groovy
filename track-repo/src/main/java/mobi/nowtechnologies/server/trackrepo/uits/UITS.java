package mobi.nowtechnologies.server.trackrepo.uits;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;


public class UITS {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IMP4Manager mp4Manager;
    private MP3Manager mp3Manager;
    private Resource privateKey;

    public void process(String inputFileName, String audioFileName, String headerFileName, String encodedFileName, boolean encrypt) {
        logger.debug("Start process : inputFileName {}, audioFileName {}, headerFileName {}, encodedFileName {}, encrypt {}", inputFileName, audioFileName, headerFileName, encodedFileName, encrypt);

        UitsParameters params = createUitsParameters();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputFileName);
            out = new FileOutputStream(audioFileName);
            if (isMP3(inputFileName)) {
                String hash = mp3Manager.getMP3MediaHash(inputFileName);
                mp3Manager.process(in, out, params, hash);
            } else {
                // Assume AAC
                mp4Manager.process(inputFileName, audioFileName, headerFileName, encodedFileName, params, null, encrypt);
            }
            logger.debug("Finish process");
        } catch (IOException e) {
            logger.error("IO exception, message : {}", e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private boolean isMP3(String inputFileName) {
        return inputFileName.endsWith(".mp3") || inputFileName.endsWith(".MP3");
    }

    private UitsParameters createUitsParameters() {
        UitsParameters params = new UitsParameters();
        params.setCopyright("All right reserved");
        params.setDistributor("ChartsNow.mobi");
        params.setIsrc("GB7438783784");
        params.setProductId("hfdjhffdjhfdjdf");
        params.setUrl("http://musicqubed.com");
        params.setUser("cntest3@cn.mobi");
        try {
            File privateKeyFile = privateKey.getFile();
            logger.debug("Loading key : {}", privateKeyFile.getAbsolutePath());
            DataInputStream dis = new DataInputStream(new FileInputStream(privateKeyFile));
            byte[] privateKeyBytes = new byte[(int) privateKeyFile.length()];
            dis.readFully(privateKeyBytes);
            dis.close();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // decode private key
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(encodedKeySpec);
            params.setKey(rsaPrivateKey);

        } catch (InvalidKeySpecException e) {
            logger.error("Invalid key spec, message : {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IO exception, message : {}", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm, message : {}", e.getMessage(), e);
        }
        return params;
    }

    public void setMp4Manager(IMP4Manager iMp4Manager) {
        this.mp4Manager = iMp4Manager;
    }

    public void setMp3Manager(MP3Manager mp3Manager) {
        this.mp3Manager = mp3Manager;
    }

    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }
}
