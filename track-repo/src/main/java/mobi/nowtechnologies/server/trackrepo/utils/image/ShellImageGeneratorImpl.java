package mobi.nowtechnologies.server.trackrepo.utils.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommand;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class ShellImageGeneratorImpl implements ImageGenerator {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ShellImageGeneratorImpl.class);
	
	public List<String> generateThumbnails(String sourceFilePath, String isrc, boolean isVideo) throws IOException, InterruptedException {
		
		LOGGER.debug("ShellImageGeneratorImpl.generateThumbnails started");
		
		List<String> result = new ArrayList<String>(thumbnails.size());
		
		for (ThumbnailType thumbnailType : thumbnails) {
			result.add(generateImage(sourceFilePath, isrc, thumbnailType, isVideo));
		}

		LOGGER.debug("ShellImageGeneratorImpl.generateThumbnails successfuly finished");
		
		return result;
	}
	
	public List<String> generateThumbnailsWithWatermark(String sourceFilePath, String isrc, boolean isVideo) throws IOException, InterruptedException {
		
		LOGGER.debug("ShellImageGeneratorImpl.generateThumbnailsWithWatermark started");
		
		List<String> result = new ArrayList<String>(thumbnails.size());
		
		for (ThumbnailType thumbnailType : thumbnails) {
			
			String resultFileName = generateImage(sourceFilePath, isrc, thumbnailType, isVideo);
			if (!isVideo) {
				coverPreviewImage(resultFileName, thumbnailType);
			}
			result.add(resultFileName);
		}
		
		LOGGER.debug("ShellImageGeneratorImpl.generateThumbnailsWithWatermark successfuly finished");
		
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
	
	protected void coverPreviewImage(String fileName, ThumbnailType type) throws IOException, InterruptedException {
		
		if (type.getCoverFilePath() == null || 
			type.getCoverFilePath().getFile() == null ||
			!type.getCoverFilePath().getFile().exists()) {
			
			return;
		}
		
		commandCoverPreviewImage.executeCommand(compositePath.getFile().getAbsolutePath(),
												type.getCoverFilePath().getFile().getAbsolutePath(),
												fileName,
												fileName);
	}
	
	private Resource imagesDir;
	private List<ThumbnailType> thumbnails;
	private ExternalCommand commandResizeImage;
	private ExternalCommand commandCoverPreviewImage;
	private Resource convertPath;
	private Resource compositePath;

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
	public void setCommandCoverPreviewImage(ExternalCommand commandCoverPreviewImage) {
		this.commandCoverPreviewImage = commandCoverPreviewImage;
	}
	public void setCompositePath(Resource compositePath) {
		this.compositePath = compositePath;
	}
}
