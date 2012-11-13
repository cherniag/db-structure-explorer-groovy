package mobi.nowtechnologies.server.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLog;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The class <code>MediaLogDaoTest</code> contains tests for the class <code>{@link MediaLogDao}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class MediaLogDaoTest {
	private static MediaLogDao mediaLogDao;
	/**
	 * Run the List<MediaLogShallow> findPurchasedTracksByUserId(int) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testFindPurchasedTracksByUserId()
		throws Exception {
		int userId = 91;

		List<MediaLog> result = mediaLogDao.findPurchasedTracksByUserId(userId);

		assertNotNull(result);
	}

	/**
	 * Run the boolean isUserAlreadyDownloadOriginal(String,int) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	@Ignore
	public void testIsUserAlreadyDownloadOriginalTrue()
		throws Exception {
		
		String selectedMediaIsrc = "GB-GFL-89-12345";
		int userId = 98781;

		boolean result = mediaLogDao.isUserAlreadyDownloadOriginal(selectedMediaIsrc, userId);
		assertTrue(result);
	}

	/**
	 * Run the boolean isUserAlreadyDownloadOriginal(String,int) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testIsUserAlreadyDownloadOriginalFalse()
		throws Exception {
		String selectedMediaIsrc = "US-UM7-11-00061";
		int userId = 98781;

		boolean result = mediaLogDao.isUserAlreadyDownloadOriginal(selectedMediaIsrc, userId);
		assertFalse(result);
	}

	/**
	 * Run the boolean isUserAlreadyDownloadOriginal(String,int) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test(expected = PersistenceException.class)
	public void testIsUserAlreadyDownloadOriginalWrongSelectedMediaIsrc()
		throws Exception {
		String selectedMediaIsrc = null;
		int userId = 1;

		mediaLogDao.isUserAlreadyDownloadOriginal(selectedMediaIsrc, userId);
	}

	/**
	 * Run the void logMediaEvent(int,int,byte) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	@Ignore
	public void testLogMediaEvent()
		throws Exception {
		int userId = 1;
		Media media = new Media();
		media.setI(1);
		byte mediaLogType = (byte) 1;

		mediaLogDao.logMediaEvent(userId, media, mediaLogType);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testLogMediaEventWrongMediaId()
		throws Exception {
		
		int userId = 1;
		Media media = null;
		byte mediaLogType = (byte) 1;

		mediaLogDao.logMediaEvent(userId, media, mediaLogType);

	}

	/**
	 * Run the void logMediaEvent(int,int,byte) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testLogMediaEventWrongMediaLogType()
		throws Exception {
		int userId = 1;
		Media media = new Media();
		media.setI(1);
		byte mediaLogType = (byte) -1;

		mediaLogDao.logMediaEvent(userId, media, mediaLogType);

	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 15.08.11 17:26
	 */
	@BeforeClass
	public static void setUp()
		throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml" });
		mediaLogDao = (MediaLogDao) appContext
				.getBean("persistence.MediaLogDao");
	
		
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 15.08.11 17:26
	 */
	@AfterClass
	public static void tearDown()
		throws Exception {
	}
}