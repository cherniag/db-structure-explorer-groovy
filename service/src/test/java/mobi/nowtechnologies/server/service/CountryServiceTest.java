package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Ignore;
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
 * The class <code>CountryServiceTest</code> contains tests for the class <code>{@link CountryService}</code>.
 *
 * @generatedBy CodePro at 16.08.11 11:54
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/service-test.xml", "classpath:/META-INF/dao-test.xml","/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class CountryServiceTest {
	
	@Resource(name = "service.CountryService")
	private CountryService countryService;

	/**
	 * Run the Integer findIdByFullName(String) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testFindIdByFullNameGreat_Britain()
		throws Exception {
		String countryFullName = "Great Britain";

		Integer result = countryService.findIdByFullName(countryFullName);
		assertNotNull(result);
	}

	/**
	 * Run the Integer findIdByFullName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 16.08.11 11:54
	 */
	@Test(expected=ServiceException.class)
	public void testFindIdByFullNameWrongCountryFullName()
		throws Exception {
		String countryFullName = "1";
		countryService.findIdByFullName(countryFullName);
	}

	/**
	 * Run the Integer findIdByFullName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 16.08.11 11:54
	 */
	@Test(expected=ServiceException.class)
	public void testFindIdByFullNameWhenCountryFullNameIsNull()
		throws Exception {
		String countryFullName = null;

		Integer result = countryService.findIdByFullName(countryFullName);
		assertNotNull(result);
	}

	/**
	 * Run the List<Country> getAllCountries() method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testGetAllCountries()
		throws Exception {
		List<Country> result = countryService.getAllCountries();
		assertNotNull(result);
	}
}