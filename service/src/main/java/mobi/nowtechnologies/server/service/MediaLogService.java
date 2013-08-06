package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.MediaLogDao;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLog;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.web.PurchasedTrackDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaLogService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaLogService.class);
	private MediaLogDao mediaLogDao;

	public void setMediaLogDao(MediaLogDao mediaLogDao) {
		this.mediaLogDao = mediaLogDao;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void logMediaEvent(int userId, Media media, byte mediaLogType) {
		if (media == null)
			throw new ServiceException("The parameter media is null");
		if (mediaLogType < 0)
			throw new ServiceException("The parameter mediaLogType < 0");
		mediaLogDao.logMediaEvent(userId, media, mediaLogType);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<PurchasedTrackDto> getPurchasedTracksByUserId(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		List<MediaLog> mediaLogShallows = mediaLogDao.findPurchasedTracksByUserId(userId);
		List<PurchasedTrackDto> purchasedTrackDtos=MediaLog.toPurchasedTrackDtoList(mediaLogShallows);
		LOGGER.debug("Output parameter purchasedTrackDtos=[{}]", purchasedTrackDtos);
		return purchasedTrackDtos;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public boolean isUserAlreadyDownloadOriginal(int mediaId, int userId) {
		LOGGER.debug("input parameters mediaId, userId: [{}], [{}]", mediaId, userId);
		
		boolean isUserAlreadyDownloadOriginal = mediaLogDao.isUserAlreadyDownloadOriginal(mediaId, userId);
		LOGGER.debug("Output parameter isUserAlreadyDownloadOriginal=[{}]", isUserAlreadyDownloadOriginal);
		return isUserAlreadyDownloadOriginal;
	}

}
