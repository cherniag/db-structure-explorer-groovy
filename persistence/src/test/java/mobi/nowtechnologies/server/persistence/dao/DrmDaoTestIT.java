package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.Drm;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>DrmDaoTest</code> contains tests for the class
 * <code>{@link DrmDao}</code>.
 * 
 * @generatedBy CodePro at 20.12.11 13:47
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class DrmDaoTestIT {

	@Resource(name = "persistence.DrmDao")
	private DrmDao drmDao;

	/**
	 * Run the List<Drm> findDrmTree(int,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.12.11 13:47
	 */
	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testFindDrmTree_incorrectParam() throws Exception {
		int userId = 1;
		String isrc = null;

		drmDao.findDrmTree(userId, isrc);
	}
	
	@Test
	public void testFindDrmTree_success() throws Exception {
		int userId = 6;
		String isrc = "USAT21001886";

		List<Drm> drms = drmDao.findDrmTree(userId, isrc);
		Assert.assertNotNull(drms);
		Assert.assertEquals(1,drms.size());
	}
}