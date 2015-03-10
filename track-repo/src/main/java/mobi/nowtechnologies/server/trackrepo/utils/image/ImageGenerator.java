package mobi.nowtechnologies.server.trackrepo.utils.image;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ImageGenerator {

    List<File> generateThumbnails(File dir, String sourceFilePath, String trackId, boolean isVideo) throws IOException, InterruptedException;

    List<File> generateThumbnailsWithWatermark(File dir, String sourceFilePath, String trackId, boolean isVideo) throws IOException, InterruptedException;
}
