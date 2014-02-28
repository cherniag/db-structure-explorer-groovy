package mobi.nowtechnologies.server.persistence.dao;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Titov Mykhaylo (titov)
 */
@Ignore
public class MediaDaoIT {
	private static MediaDao mediaDao;

	@Test
	@Ignore
	public void testIsBalanceOk_StatusTrue()
		throws Exception {
		int aUserId = 3;
		int aMediaUID = 47;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertTrue(result);
	}

	@Test
	public void testIsBalanceOk_StatusFalse()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertFalse(result);
	}

	@Test
	@Ignore
	public void testIsBalanceOk_3()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertTrue(result);
	}

	@Test(expected = PersistenceException.class)
	public void testIsBalanceOk_4()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = -1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertTrue(result);
	}

	@BeforeClass
	public static void setUp()
		throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] {
		"/META-INF/dao-test.xml"});
		mediaDao = (MediaDao) appContext.getBean("persistence.MediaDao");
	}
	
	@Test
	public void testConditionalUpdateByUserAndMedia()
		throws Exception {
		int userId = 98736;
		int mediaId = 51;
		mediaDao.conditionalUpdateByUserAndMedia(userId, mediaId);
	}
}