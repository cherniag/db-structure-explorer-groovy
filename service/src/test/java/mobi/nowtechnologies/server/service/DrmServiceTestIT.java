package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>DrmServiceTest</code> contains tests for the class <code>{@link DrmService}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class DrmServiceTestIT {
	/**
	 * An instance of the class being tested.
	 *
	 * @see DrmService
	 *
	 */
	@Resource(name="service.DrmService")
	private DrmService drmService;

	/**
	 * Run the Object[] getDrm(Integer[],int,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test
	public void testGetDrmSuccess() throws Exception {
		int userId = 6;
		String isrc = "USAT21001886";
		byte drmValue=5;
		String aCommunityName = "CN Commercial Beta";

		Object[] result = drmService.processSetDrmCommand(isrc, drmValue, userId, aCommunityName);

		assertNotNull(result);
	}

	/**
	 * Run the Object[] getDrm(Integer[],int,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test(expected=ServiceException.class)
	public void testGetDrmWithCommunityNameIsNull()
		throws Exception {
		int userId = 6;
		String isrc = "USAT21001886";
		String aCommunityName = null;
		byte drmValue=5;

		Object[] result = drmService.processSetDrmCommand(isrc, drmValue, userId, aCommunityName);

		assertNotNull(result);
	}

	/**
	 * Run the Object[] getDrm(Integer[],int,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test(expected=ServiceException.class)
	public void testGetDrmWithMediaIdsIsNull()
		throws Exception {
		int userId = 6;
		String isrc = "USAT21001886";
		String aCommunityName = "CN Commercial Beta";
		byte drmValue=5;

		Object[] result = drmService.processSetDrmCommand(isrc, drmValue, userId, aCommunityName);
		
		assertNotNull(result);
	}
	
//	@Test
//	public void testBuyTrack() {
//		
//		drmService.buyTrack(user, isrc);
//	}
}