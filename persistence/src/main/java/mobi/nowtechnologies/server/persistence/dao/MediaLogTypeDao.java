/**
 * 
 */
package mobi.nowtechnologies.server.persistence.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.MediaLog;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaLogTypeDao extends JpaDaoSupport {
	public static final String DOWNLOAD = "DOWNLOAD";
	public static final String PURCHASE = "PURCHASE";
	public static final String DOWNLOAD_ORIGINAL = "DOWNLOAD_ORIGINAL";
	private Map<String, MediaLogType> unmodifableMapMediaLogTypes;
	private EntityDao entityDao;
	
	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	@SuppressWarnings("unchecked")
	public List<String> findStatusNamesByUserIdAndMediaId(int userId,
			int mediaId) {
		if (mediaId < 0)
			throw new PersistenceException("The parameter mediaId < 0");
		return (List<String>) getJpaTemplate()
				.find(
						"select mediaLogType.name from "
								+ MediaLogType.class.getSimpleName()
								+ " mediaLogType, "
								+ MediaLog.class.getSimpleName()
								+ " mediaLog where mediaLogType.i = mediaLog.logType and "
								+ " mediaLog.userUID = ?1 and mediaLog.mediaUID = ?2)",
						userId, mediaId);
	}

	public Map<String, MediaLogType> getMediaLogTypes() {
		if (unmodifableMapMediaLogTypes == null) {
			Map<String, MediaLogType> mediaLogTypes = new HashMap<String, MediaLogType>();

			mediaLogTypes.put(DOWNLOAD, entityDao.findByProperty(
					MediaLogType.class, MediaLogType.Fields.name.toString(),
					DOWNLOAD));
			mediaLogTypes.put(PURCHASE, entityDao.findByProperty(
					MediaLogType.class, MediaLogType.Fields.name.toString(),
					PURCHASE));
			mediaLogTypes.put(DOWNLOAD_ORIGINAL, entityDao.findByProperty(
					MediaLogType.class, MediaLogType.Fields.name.toString(),
					DOWNLOAD_ORIGINAL));
			unmodifableMapMediaLogTypes = Collections
					.unmodifiableMap(mediaLogTypes);
		}
		return unmodifableMapMediaLogTypes;
	}
}
