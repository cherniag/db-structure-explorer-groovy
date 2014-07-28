package mobi.nowtechnologies.java.server.uits;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Oleg Artomov on 7/1/2014.
 */
public interface MP4ManagerIntf {
    @SuppressWarnings("unused")
    int process(InputStream audioFile, OutputStream data, OutputStream header, OutputStream encoded, UitsParameters params, String md5, boolean encrypt);

    int process(String inputFile, String audioFile, String headerFile, String encodedFile, UitsParameters params, String md5, boolean encrypt) throws IOException;

    int processHeader(InputStream header, OutputStream out, UitsParameters params, String md5);

    String getMediaHash(InputStream audioFile);
}
