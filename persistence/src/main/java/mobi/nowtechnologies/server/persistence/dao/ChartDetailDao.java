package mobi.nowtechnologies.server.persistence.dao;

import java.util.Collection;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.DrmType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class ChartDetailDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailDao.class);

	
	@SuppressWarnings("unchecked")
	public List<ChartDetail> findChartDetailTreeForDrmUpdate(int userId, byte chartId) {
		LOGGER.debug("input parameters userId, chartId: [{}], [{}]",  userId, chartId);
		
		List<ChartDetail> chartDetails = getJpaTemplate().findByNamedQuery("ChartDetail.findChartDetailTreeForDrmUpdate", chartId);
		LOGGER.debug("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}
	
	public boolean isTrackCanBeBoughtAccordingToLicense(String isrc){
		if (isrc == null)
			throw new PersistenceException("The parameter isrc is null");
		LOGGER.debug("input parameters communityId, isrc: [{}]", isrc);
		
		Object[] argArray = new Object[] { isrc, Utils.getEpochSeconds()};
		Boolean isTrackCanBeBoughtAccordingToLicense = (1==(Long)getJpaTemplate().findByNamedQuery(ChartDetail.NQ_IS_TRACK_CAN_BE_BOUGHT_ACCORDING_TO_LICENSE, argArray).get(0));
		
		LOGGER.debug("Output parameter isTrackCanBeBoughtAccordingToLicense=[{}]", isTrackCanBeBoughtAccordingToLicense);
		return isTrackCanBeBoughtAccordingToLicense;
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<ChartDetail> findContentInfoByDrmType(User user, DrmType drmType) {
		LOGGER.debug("input parameters user, drmType: [{}], [{}]", user, drmType);
		final Object[] values = new Object[] { user.getUserGroup().getChart(), drmType, user };
		List<ChartDetail> chartDetails = getJpaTemplate().findByNamedQuery(ChartDetail.NQ_FIND_CONTENT_INFO_BY_DRM_TYPE, values);
		LOGGER.debug("Output parameter [{}]", chartDetails);
		return chartDetails;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<ChartDetail> findContentInfoByIsrc(User user, Collection<String> isrcs) {
		final Object[] values = new Object[] {user.getUserGroup().getChart().getI(), isrcs, user};
		return getJpaTemplate().findByNamedQuery(ChartDetail.NQ_FIND_CONTENT_INFO_BY_ISRC, values);
	}
}
