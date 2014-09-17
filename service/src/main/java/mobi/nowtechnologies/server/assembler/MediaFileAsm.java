package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.shared.dto.admin.MediaFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
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
		mediaFileDto.setId(mediaFile.getI());
		mediaFileDto.setSize(mediaFile.getSize());
		mediaFileDto.setDuration(mediaFile.getDuration());
        mediaFileDto.setFileType(FileType.findById(mediaFile.getFileType().getI()));
		
		LOGGER.info("Output parameter mediaFileDto=[{}]", mediaFileDto);
		return mediaFileDto;

	}

}
