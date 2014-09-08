package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CountryAppVersionDaoTest extends AbstractRepositoryIT {
    @Resource
	private CountryAppVersionDao countryAppVersionDao;

	@Test
	public void testIsAppVersionLinkedWithCountry_EmptyParammeters()
		throws Exception {
		String appVersion = "";
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	@Test
	public void testIsAppVersionLinkedWithCountry_Success()
		throws Exception {
		
		String appVersion = "CNBETA";
		String countryCode = "GB";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertTrue(result);
	}

	@Test
	public void testIsAppVersionLinkedWithCountry_appVersionIsEmpty()
		throws Exception {
		
		String appVersion = "CNBETA";
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	@Test
	public void testIsAppVersionLinkedWithCountry_countryCodeIsEmpty()
		throws Exception {
		
		String appVersion = "";
		String countryCode = "GB";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);
		assertFalse(result);
	}

	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testIsAppVersionLinkedWithCountry_5()
		throws Exception {
		
		String appVersion = null;
		String countryCode = "";

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);

		assertFalse(result);
	}

	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testIsAppVersionLinkedWithCountry_6()
		throws Exception {
		
		String appVersion = "";
		String countryCode = null;

		boolean result = countryAppVersionDao.isAppVersionLinkedWithCountry(appVersion, countryCode);

		assertTrue(result);
	}
}