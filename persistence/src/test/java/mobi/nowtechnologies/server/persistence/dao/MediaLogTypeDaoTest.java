package mobi.nowtechnologies.server.persistence.dao;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The class <code>MediaLogTypeDaoTest</code> contains tests for the class
 * <code>{@link MediaLogTypeDao}</code>.
 * 
 * @generatedBy CodePro at 01.07.11 10:36
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class MediaLogTypeDaoTest {
	private static MediaLogTypeDao mediaLogTypeDao;
	
	public static final String DOWNLOAD = "DOWNLOAD";
	public static final String PURCHASE = "PURCHASE";

	/**
	 * Run the List<MediaLogType> findByUserIdAndMediaId(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 01.07.11 10:36
	 */
	@Test
	public void testFindByUserIdAndMediaId_NotEmptyResult() throws Exception {
		int aUserId = 1;
		int aMediaUID = 47;

		List<String> result = mediaLogTypeDao.findStatusNamesByUserIdAndMediaId(aUserId,
				aMediaUID);

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	/**
	 * Run the List<MediaLogType> findByUserIdAndMediaId(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 01.07.11 10:36
	 */
	@Test
	public void testFindByUserIdAndMediaId_EmptyResult() throws Exception {
		int aUserId = 0;
		int aMediaUID = 1;

		List<String> result = mediaLogTypeDao.findStatusNamesByUserIdAndMediaId(aUserId,
				aMediaUID);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	/**
	 * Run the List<MediaLogType> findByUserIdAndMediaId(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 01.07.11 10:36
	 */
	@Test(expected = PersistenceException.class)
	public void testFindByUserIdAndMediaId_WrongMediaUid() throws Exception {
		int aUserId = 1;
		int aMediaUID = -1;

		List<String> result = mediaLogTypeDao.findStatusNamesByUserIdAndMediaId(aUserId,
				aMediaUID);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 01.07.11 10:36
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml" });
		mediaLogTypeDao = (MediaLogTypeDao) appContext
				.getBean("persistence.MediaLogTypeDao");
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 01.07.11 10:36
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		// Add additional tear down code here
	}
}