package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLog;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class MediaLogDao extends JpaDaoSupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaLogDao.class);
	private MediaLogTypeDao mediaLogTypeDao;
	private EntityDao entityDao;

	public void setMediaLogTypeDao(MediaLogTypeDao mediaLogTypeDao) {
		this.mediaLogTypeDao = mediaLogTypeDao;
	}

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public void logMediaEvent(int userId, Media media, byte mediaLogType) {
		if (media == null)
			throw new IllegalArgumentException("The parameter media is null");
		if (mediaLogType < 0)
			throw new IllegalArgumentException(
					"The parameter mediaLogType < 0");
		MediaLog mediaLog = new MediaLog();
		mediaLog.setMedia(media);
		mediaLog.setUserUID(userId);
		mediaLog.setLogTimestamp((int) (System.currentTimeMillis() / 1000));
		mediaLog.setLogType(mediaLogType);
		entityDao.saveEntity(mediaLog);
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaLog> findPurchasedTracksByUserId(final int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		MediaLogType mediaLogTypePurchased = mediaLogTypeDao.getMediaLogTypes().get(MediaLogTypeDao.PURCHASE);
		List<MediaLog> mediaLogShallows = getJpaTemplate().findByNamedQuery(MediaLog.NQ_GET_PURCHASED_TRACKS_BY_USER_ID, (byte)mediaLogTypePurchased.getI(), userId);

		LOGGER.debug("Output parameter mediaLogShallows=[{}]", mediaLogShallows);
		return mediaLogShallows;
	}

	@SuppressWarnings("unused")
	public boolean isUserAlreadyDownloadOriginal(int mediaId, int userId) {
		LOGGER.debug("input parameters mediaId, userId: [{}], [{}]", mediaId, userId);
		
		MediaLogType downloadOriginalMediaLogType = mediaLogTypeDao.getMediaLogTypes().get(
				MediaLogTypeDao.DOWNLOAD_ORIGINAL);
		
		Object[] values = {(byte)downloadOriginalMediaLogType.getI(), userId,mediaId}; 
		Long count = (Long)getJpaTemplate().findByNamedQuery(MediaLog.NQ_IS_DOWNLOADED_ORIGINAL, values).get(0);
		boolean isUserAlreadyDownloadOriginal = (count == 1L);
		LOGGER.debug("Output parameter isUserAlreadyDownloadOriginal=[{}]", isUserAlreadyDownloadOriginal);
		return isUserAlreadyDownloadOriginal;
	}
}
