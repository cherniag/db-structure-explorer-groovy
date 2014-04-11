package mobi.nowtechnologies.server.trackrepo.utils.image;

import java.io.IOException;
import java.util.List;

public interface ImageGenerator {

	List<String> generateThumbnails(String sourceFilePath, String isrc, boolean isVideo) throws IOException, InterruptedException;
	
	List<String> generateThumbnailsWithWatermark(String sourceFilePath, String isrc, boolean isVideo) throws IOException, InterruptedException;
}