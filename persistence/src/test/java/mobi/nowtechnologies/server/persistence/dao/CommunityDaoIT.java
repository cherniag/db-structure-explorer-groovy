package mobi.nowtechnologies.server.persistence.dao;// @author myti on 28.07.2014.

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class CommunityDaoIT {
    @Test
    public void testGetCOMMUNITY_MAP_NAME_AS_KEY()
            throws Exception {

        Map<String, Community> result = CommunityDao.getMapAsNames();
        assertNotNull(result);
    }

    @Test
    public void testGetCOMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY()
            throws Exception {

        Map<String, Community> result = CommunityDao.getMapAsUrls();
        assertNotNull(result);
    }
}