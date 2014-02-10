package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Titov Mykhaylo (titov)
 */

public class ChartDetailRepositoryIT extends AbstractRepositoryIT {
	
	@Resource(name="chartDetailRepository")
	ChartDetailRepository chartDetailRepository;
	
	@Resource(name="communityRepository")
	CommunityRepository communityRepository;
	
	@Test
	public void testFindNearestLatestPublishDate_Success(){
		
		Community community = communityRepository.findByRewriteUrlParameter("nowtop40");
		
		Long choosedPublishTimeMillis = 1343806800000L;
		
		Long nearestLatestPublishDate = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis , community);
		
		assertNotNull(nearestLatestPublishDate);
		assertEquals(choosedPublishTimeMillis, nearestLatestPublishDate);
		
	}
	
}