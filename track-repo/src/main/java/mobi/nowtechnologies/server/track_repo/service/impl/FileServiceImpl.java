package mobi.nowtechnologies.server.track_repo.service.impl;

import java.io.File;
import java.io.IOException;

import mobi.nowtechnologies.server.track_repo.assembler.FileAsm;
import mobi.nowtechnologies.server.track_repo.domain.AssetFile;
import mobi.nowtechnologies.server.track_repo.dto.AssetFileDto;
import mobi.nowtechnologies.server.track_repo.repository.FileRepository;
import mobi.nowtechnologies.server.track_repo.service.FileService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class FileServiceImpl implements FileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

	private FileRepository fileRepository;
	
	public void setFileRepository(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	@Override
	public AssetFileDto getFile(Long id) {
		LOGGER.debug("input getFile(id): [{}]", new Object[] { id });
		
		AssetFile file = fileRepository.findOne(id);
		if(file == null)
			return null;
		
		AssetFileDto fileDto = FileAsm.toAssetFileDto(file);
		try {
			byte[] content = FileUtils.readFileToByteArray(new File(file.getPath()));
			fileDto.setContent(content);
		} catch (IOException e) {
			LOGGER.error("Can't read file.",e);
		}

		LOGGER.debug("output getFile(id): [{}]", new Object[] { fileDto });
		return fileDto;
	}
}
