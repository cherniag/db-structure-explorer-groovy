package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Country;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/service-test.xml", "classpath:/META-INF/dao-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class CountryServiceIT {

    @Resource(name = "service.CountryService")
    private CountryService countryService;

    @Test
    public void testFindIdByFullNameGreat_Britain() throws Exception {
        String name = "GB";

        Country c = countryService.findIdByName(name);
        assertNotNull(c);
    }

    @Test
    public void testGetAllCountries() throws Exception {
        List<Country> result = countryService.getAllCountries();
        assertNotNull(result);
    }
}