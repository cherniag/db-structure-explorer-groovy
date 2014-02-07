package mobi.nowtechnologies.server.trackrepo.utils.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommand;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

public class ShellImageGeneratorImpl implements ImageGenerator {

	public List<String> generateThumbnails(String sourceFilePath, String isrc, boolean isVideo) throws IOException, InterruptedException {
		
		List<String> result = new ArrayList<String>(thumbnails.size());
		
		for (ThumbnailType thumbnailType : thumbnails) {
			result.add(generateImage(sourceFilePath, isrc, thumbnailType, isVideo));
		}
		
		return result;
	}

	protected String generateImage(String sourceFilePath, String isrc, ThumbnailType thumbnailType, boolean isVideo) throws IOException, InterruptedException {
		
		if (StringUtils.isBlank(sourceFilePath)) {
			throw new RuntimeException("Image Generation: source file path is null or emmpty");
		}
		if (StringUtils.isBlank(isrc)) {
			throw new RuntimeException("Image Generation: ISRC is null or empty");
		}
		
	    String resultFilePath = imagesDir.getFile().getAbsolutePath() + "/" + isrc + thumbnailType.getFileNameTail() + "." + thumbnailType.getFileExtension();
	    
	    int imageSize = isVideo ? thumbnailType.getImageSizeForVideo() : thumbnailType.getImageSizeForAudio();

	    commandResizeImage.executeCommand(convertPath.getFile().getAbsolutePath(),
	    								  sourceFilePath,
	    								  "" + imageSize,
	    								  "" + imageSize,
	    								  thumbnailType.getAdditionalParams() == null ? "" : thumbnailType.getAdditionalParams(),
	    								  resultFilePath);
	    return resultFilePath;
	}
	
	private Resource imagesDir;
	private List<ThumbnailType> thumbnails;
	private ExternalCommand commandResizeImage;
	private Resource convertPath;

	public void setImagesDir(Resource imagesDir) {
		this.imagesDir = imagesDir;
	}
	public void setThumbnails(List<ThumbnailType> thumbnails) {
		this.thumbnails = thumbnails;
	}
	public void setCommandResizeImage(ExternalCommand commandResizeImage) {
		this.commandResizeImage = commandResizeImage;
	}
	public void setConvertPath(Resource convertPath) {
		this.convertPath = convertPath;
	}

	@Override
	public List<String> generateThumbnailsWithWatermark(String isrc) {
		return Collections.emptyList();
	}
}
