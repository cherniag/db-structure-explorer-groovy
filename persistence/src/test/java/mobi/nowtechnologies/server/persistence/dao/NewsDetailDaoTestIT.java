package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.NewsDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * The class <code>NewsDetailDaoTestIT</code> contains tests for the class
 * <code>{@link NewsDetailDao}</code>.
 * 
 * @generatedBy CodePro at 21.12.11 13:38
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class NewsDetailDaoTestIT {

	@Resource(name = "persistence.NewsDetailDao")
	private NewsDetailDao newsDetailDao;

	
	@Test
	public void test_getNewsDetail_Success() throws Exception {
		byte communityId = 5;
		
		List<NewsDetail> newsDetails = newsDetailDao.getNewsDetails(communityId);
		assertNotNull(newsDetails);
	}
}