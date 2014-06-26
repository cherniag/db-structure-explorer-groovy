package mobi.nowtechnologies.java.server.uits;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created by Oleg Artomov on 6/26/2014.
 */
public class Mp4Mock {

    public void execute(String inputFile, String encodedFile, String audioFile, String headerFile) throws IOException {
        Files.copy(new File(inputFile), new File(encodedFile));
        Files.copy(new File(inputFile), new File(audioFile));
        Files.copy(new File(inputFile), new File(headerFile));
    }
}
