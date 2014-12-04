package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class DrmDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(DrmDao.class);
	
	public List<Drm> findDrmTree(int userId, String isrc) {
		if (isrc == null)
			throw new PersistenceException("The parameter isrc is null");
		Object[] argArray = new Object[] { userId, isrc };
		LOGGER.debug("input parameters userId, isrc: [{}], [{}]", userId, isrc);
		
		List<Drm> drms = getJpaTemplate().findByNamedQuery("Drm.findDrmTree", argArray);

		LOGGER.debug("Output parameter drms=[{}]", drms);
		return drms;
	}

	public List<Drm> findDrmAndDrmTypeTree(int userId) {
		LOGGER.debug("input parameters userId", userId);
		List<Drm> drms = getJpaTemplate().findByNamedQuery("Drm.findDrmAndDrmTypeTree", userId);
		LOGGER.debug("Output parameter drms=[{}]", drms);
		return drms;
	}
}
