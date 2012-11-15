package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.shared.dto.admin.MediaFileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class MediaFileAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileAsm.class);
	
	public static MediaFileDto toMediaFileDto(MediaFile mediaFile) {
		LOGGER.debug("input parameters mediaFile: [{}]", mediaFile);

		MediaFileDto mediaFileDto = new MediaFileDto();
		
		mediaFileDto.setFilename(mediaFile.getFilename());
		//mediaFileDto.setFileType(FileType.valueOf(mediaFile.getFileType().getName()));
		mediaFileDto.setId(mediaFile.getI());
		mediaFileDto.setSize(mediaFile.getSize());
		
		LOGGER.info("Output parameter mediaFileDto=[{}]", mediaFileDto);
		return mediaFileDto;
		
	}

}
