package mobi.nowtechnologies.server.persistence.dao;

import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
		.getLogger(MediaDao.class);
	
	private EntityDao entityDao;

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	public boolean isBalanceOk(int userId, int mediaId) {
		if (mediaId < 0)
			throw new PersistenceException("The parameter aMediaUID < 0");
		Long status = (Long) getJpaTemplate().find(
						"select count(*) from "
								+ Media.class.getSimpleName()
								+ " media, "
								+ User.class.getSimpleName()
								+ " user where media.price < (user.subBalance + user.freeBalance)"
								+ " and user.id = ?1 and media.i = ?2",
						userId, mediaId).get(0);
		return Long.valueOf(1L).equals(status);
	}
	
	public void conditionalUpdateByUserAndMedia(int userId, int mediaId) {
		List<?> list = getJpaTemplate().find(
				"select drm from " + Drm.class.getSimpleName()
				+ " drm where drm.userId = ?1 and drm.mediaId = ?2 and timestamp = 0", userId, mediaId);
		if (list != null && list.size() > 0) {
			Drm drm = (Drm) list.get(0);
			drm.setTimestamp(getEpochSeconds());
			entityDao.updateEntity(drm);
		}
	}
}
