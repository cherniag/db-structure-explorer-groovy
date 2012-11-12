package mobi.nowtechnologies.server.persistence.dao;

import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * The class <code>CountryAppVersionDaoTest</code> contains tests for the class <code>{@link CountryAppVersionDao}</code>.
 *

 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class CountryAppVersionDaoTest {
	private static CountryAppVersionDao countryAppVersionDao;

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test
	public void testIsAppVersionLinkedWithCountry_EmptyParammeters()
		throws Exception {
		String appVersion = "";
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test
	public void testIsAppVersionLinkedWithCountry_Success()
		throws Exception {
		
		String appVersion = "CNBETA";
		String countryCode = "GB";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertTrue(result);
	}

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test
	public void testIsAppVersionLinkedWithCountry_appVersionIsEmpty()
		throws Exception {
		
		String appVersion = "CNBETA";
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test
	public void testIsAppVersionLinkedWithCountry_countryCodeIsEmpty()
		throws Exception {
		
		String appVersion = "";
		String countryCode = "GB";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testIsAppVersionLinkedWithCountry_5()
		throws Exception {
		
		String appVersion = null;
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);

		assertFalse(result);
	}

	/**
	 * Run the boolean isAppVersionLinkedWithCountry(String,String) method test.
	 *
	 * @throws Exception
	 *
	
	 */
	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testIsAppVersionLinkedWithCountry_6()
		throws Exception {
		
		String appVersion = "";
		String countryCode = null;

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);

		assertTrue(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	
	 */
	@BeforeClass
	public static void setUp()
		throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml" });
		countryAppVersionDao = (CountryAppVersionDao) appContext.getBean("persistence.CountryAppVersionDao");
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	
	 */
	@AfterClass
	public static void tearDown()
		throws Exception {
	}
}