package mobi.nowtechnologies.server.trackrepo.uits;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Oleg Artomov on 7/1/2014.
 */
public interface IMP4Manager {

    @SuppressWarnings("unused")
    int process(InputStream audioFile, OutputStream data, OutputStream header, OutputStream encoded, UitsParameters params, String md5, boolean encrypt);

    int process(String inputFile, String audioFile, String headerFile, String encodedFile, UitsParameters params, String md5, boolean encrypt) throws IOException;
}
