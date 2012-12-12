package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaLogTypeService {

	private MediaLogTypeDao mediaLogTypeDao;

	public void setMediaLogTypeDao(MediaLogTypeDao mediaLogTypeDao) {
		this.mediaLogTypeDao = mediaLogTypeDao;
	}

	public List<String> findNameByUserIdAndMediaId(int userId, int mediaId) {
		if (mediaId < 0)
			throw new ServiceException("The parameter mediaId < 0");
		return mediaLogTypeDao.findStatusNamesByUserIdAndMediaId(userId,
				mediaId);
	}

	public Map<String, MediaLogType> getMediaLogTypes() {
		return mediaLogTypeDao.getMediaLogTypes();
	}

}
