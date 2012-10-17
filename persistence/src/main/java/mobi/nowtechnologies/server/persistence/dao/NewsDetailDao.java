package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.NewsDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

public class NewsDetailDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDetailDao.class);
	
	@SuppressWarnings("unchecked")
	public List<NewsDetail> getNewsDetails(byte newsId) {
		LOGGER.debug("input parameters newsId: [{}]", new Object[] { newsId });
		List<NewsDetail>  newsDetails = getJpaTemplate().findByNamedQuery(NewsDetail.NQ_GET_NEWS_DETAIL, Integer.valueOf(newsId));
		LOGGER.debug("Output parameter newsDetails=[{}]", newsDetails);
		return newsDetails;
	}

}
