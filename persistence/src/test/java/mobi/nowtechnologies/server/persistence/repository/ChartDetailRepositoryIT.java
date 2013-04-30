package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.Community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class ChartDetailRepositoryIT {
	
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