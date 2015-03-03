package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;

import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MediaLogTypeService {

    private MediaLogTypeDao mediaLogTypeDao;

    public void setMediaLogTypeDao(MediaLogTypeDao mediaLogTypeDao) {
        this.mediaLogTypeDao = mediaLogTypeDao;
    }

    public Map<String, MediaLogType> getMediaLogTypes() {
        return mediaLogTypeDao.getMediaLogTypes();
    }

}
