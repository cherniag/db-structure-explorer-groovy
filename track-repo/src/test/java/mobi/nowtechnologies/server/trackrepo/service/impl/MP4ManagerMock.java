package mobi.nowtechnologies.server.trackrepo.service.impl;

import com.google.common.io.Files;
import mobi.nowtechnologies.server.trackrepo.uits.IMP4Manager;
import mobi.nowtechnologies.server.trackrepo.uits.UitsParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Oleg Artomov on 7/1/2014.
 */
public class MP4ManagerMock implements IMP4Manager {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public int process(InputStream audioFile, OutputStream data, OutputStream header, OutputStream encoded, UitsParameters params, String md5, boolean encrypt) {
        return 0;
    }

    @Override
    public int process(String inputFile, String audioFile, String headerFile, String encodedFile, UitsParameters params, String md5, boolean encrypt) throws FileNotFoundException {
        try {
            Files.copy(new File(inputFile), new File(encodedFile));
            Files.copy(new File(inputFile), new File(audioFile));
            Files.copy(new File(inputFile), new File(headerFile));
        } catch (IOException e) {
            logger.error("ERROR", e);
        }
        return 0;
    }

    @Override
    public int processHeader(InputStream header, OutputStream out, UitsParameters params, String md5) {
        return 0;
    }

    @Override
    public String getMediaHash(String audioFile) {
        return null;
    }
}
