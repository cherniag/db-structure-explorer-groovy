package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import mobi.nowtechnologies.server.trackrepo.service.FileService;
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
	public AssetFile getFile(Long id) {
		LOGGER.debug("input getFile(id): [{}]", new Object[] { id });
		
		AssetFile file = fileRepository.findOne(id);
		if(file == null)
			return null;

		LOGGER.debug("output getFile(id): [{}]", new Object[] { file });
		return file;
	}
}
