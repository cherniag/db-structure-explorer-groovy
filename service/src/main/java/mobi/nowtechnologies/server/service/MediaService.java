package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.MediaDao;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaService.class);

	private MediaLogService mediaLogService;
	private MediaLogTypeService mediaLogTypeService;
	private MediaDao mediaDao;
	private EntityService entityService;
	private MediaRepository mediaRepository;

	public void setMediaLogService(MediaLogService mediaLogService) {
		this.mediaLogService = mediaLogService;
	}
	public void setMediaLogTypeService(MediaLogTypeService aMediaLogTypeService) {
		this.mediaLogTypeService = aMediaLogTypeService;
	}

	public void setMediaDao(MediaDao aMediaDao) {
		this.mediaDao = aMediaDao;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public void setMediaRepository(MediaRepository mediaRepository) {
		this.mediaRepository = mediaRepository;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void logMediaEvent(int userId, Media media, String mediaLogType) {
		final Map<String,MediaLogType> MEDIA_LOG_TYPES = mediaLogTypeService.getMediaLogTypes();
		mediaLogService.logMediaEvent(userId, media, (byte) MEDIA_LOG_TYPES.get(mediaLogType).getI());
	}

	public Media findByIsrc(String mediaIsrc) {
		if (mediaIsrc == null)
			throw new ServiceException("The parameter mediaIsrc is null");
		return entityService.findByProperty(Media.class, Media.Fields.isrc.toString(), mediaIsrc);
	}
	
	public void conditionalUpdateByUserAndMedia(int userId, int mediaId) {
		mediaDao.conditionalUpdateByUserAndMedia(userId, mediaId);
	}
	
	@Transactional(readOnly = true)
	public List<Media> getMedias(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%");
	}

    @Transactional(readOnly = true)
    public List<Media> getVideo(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%", FileType.VIDEO.getIdAsByte());
    }

    @Transactional(readOnly = true)
    public List<Media> getMusic(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%", FileType.MOBILE_AUDIO.getIdAsByte());
    }
	
	@Transactional(readOnly = true)
	public Media findById(Integer id) {
		LOGGER.debug("input parameters id: [{}]", id);
		
		Media media = mediaRepository.findOne(id);
		
		LOGGER.info("Output parameter media=[{}]", media);
		return media;
	}

}
