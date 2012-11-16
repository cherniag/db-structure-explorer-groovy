package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.DrmType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

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

	public List<Drm> findByUserAndDrmType(int userId, DrmType drmType) {
		Object[] argArray = new Object[] { userId, drmType };
		List<Drm> drms = getJpaTemplate().findByNamedQuery(Drm.NQ_FIND_BY_USER_AND_DRM_TYPE, argArray);
		return drms;
	}
}
