package mobi.nowtechnologies.server.trackrepo.utils.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

import net.coobird.thumbnailator.Thumbnails;

public class JavaImageGeneratorImpl implements ImageGenerator {


	public List<String> generateThumbnails(String sourceFilePath, String isrc, boolean isVideo) throws IOException {
		
		List<String> result = new ArrayList<String>(thumbnails.size());
		
		for (ThumbnailType thumbnailType : thumbnails) {
			result.add(generateImage(sourceFilePath, isrc, thumbnailType, isVideo));
		}
		
		return result;
	}


	public List<String> generateThumbnailsWithWatermark(String isrc) {
		return Collections.emptyList();
	}

	
	protected String generateImage(String sourceFilePath, String isrc, ThumbnailType thumbnailType, boolean isVideo) throws IOException {
		
		if (StringUtils.isBlank(sourceFilePath)) {
			throw new RuntimeException("Image Generation: source file path is null or emmpty");
		}
		if (StringUtils.isBlank(isrc)) {
			throw new RuntimeException("Image Generation: ISRC is null or empty");
		}
		
		File sourceFile = new File(sourceFilePath); 
	    File resultFile = new File(imagesDir.getFile().getAbsolutePath() + "/" + isrc + thumbnailType.getFileNameTail() + "." + thumbnailType.getFileExtension());
	    
	    int imageSize = isVideo ? thumbnailType.getImageSizeForVideo() : thumbnailType.getImageSizeForAudio();
	    
	    Thumbnails.of(sourceFile)
        		  .size(imageSize, imageSize)
        		  .toFile(resultFile);
	    
	    return resultFile.getAbsolutePath();
	}
	
	private Resource imagesDir;
	private List<ThumbnailType> thumbnails;

	public void setImagesDir(Resource imagesDir) {
		this.imagesDir = imagesDir;
	}

	public void setThumbnails(List<ThumbnailType> thumbnails) {
		this.thumbnails = thumbnails;
	}
}
