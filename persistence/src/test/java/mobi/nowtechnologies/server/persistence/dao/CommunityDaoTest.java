package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * The class <code>CommunityDaoTest</code> contains tests for the class <code>{@link CommunityDao}</code>.
 *
 * @generatedBy CodePro at 11.08.11 18:34
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class CommunityDaoTest {
	/**
	 * Run the Map<String, ImmutableCommunity> getCOMMUNITY_MAP_NAME_AS_KEY() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11.08.11 18:34
	 */
	@Test
	public void testGetCOMMUNITY_MAP_NAME_AS_KEY()
		throws Exception {

		Map<String, Community> result = CommunityDao.getMapAsNames();
		assertNotNull(result);
	}

	/**
	 * Run the Map<String, ImmutableCommunity> getCOMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11.08.11 18:34
	 */
	@Test
	public void testGetCOMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY()
		throws Exception {

		Map<String, Community> result = CommunityDao.getMapAsUrls();
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 11.08.11 18:34
	 */
	@BeforeClass
	public static void setUp()
		throws Exception {
		new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml" });
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 11.08.11 18:34
	 */
	@AfterClass
	public static void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}