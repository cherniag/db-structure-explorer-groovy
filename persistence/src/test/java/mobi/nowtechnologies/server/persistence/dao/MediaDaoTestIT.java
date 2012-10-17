package mobi.nowtechnologies.server.persistence.dao;

import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * The class <code>MediaDaoTest</code> contains tests for the class <code>{@link MediaDao}</code>.
 *
 * @generatedBy CodePro at 01.07.11 10:27
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class MediaDaoTestIT {
	private static MediaDao mediaDao;

	/**
	 * Run the boolean isBalanceOk(int,int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 10:27
	 */
	@Test
	@Ignore
	public void testIsBalanceOk_StatusTrue()
		throws Exception {
		int aUserId = 3;
		int aMediaUID = 47;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertTrue(result);
	}

	/**
	 * Run the boolean isBalanceOk(int,int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 10:27
	 */
	@Test
	public void testIsBalanceOk_StatusFalse()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertFalse(result);
	}

	/**
	 * Run the boolean isBalanceOk(int,int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 10:27
	 */
	@Test
	@Ignore
	public void testIsBalanceOk_3()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		assertTrue(result);
	}

	/**
	 * Run the boolean isBalanceOk(int,int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 10:27
	 */
	@Test(expected = PersistenceException.class)
	public void testIsBalanceOk_4()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = -1;

		boolean result = mediaDao.isBalanceOk(aUserId, aMediaUID);

		// add additional test code here
		assertTrue(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 01.07.11 10:27
	 */
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