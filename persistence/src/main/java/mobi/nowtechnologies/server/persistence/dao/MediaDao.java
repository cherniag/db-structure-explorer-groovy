package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MediaDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaDao.class);

    private EntityDao entityDao;

    public void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public void conditionalUpdateByUserAndMedia(int userId, int mediaId) {
        List<?> list = getJpaTemplate().find("select drm from " + Drm.class.getSimpleName() + " drm where drm.userId = ?1 and drm.mediaId = ?2 and timestamp = 0", userId, mediaId);
        if (list != null && list.size() > 0) {
            Drm drm = (Drm) list.get(0);
            drm.setTimestamp(getEpochSeconds());
            entityDao.updateEntity(drm);
        }
    }
}
