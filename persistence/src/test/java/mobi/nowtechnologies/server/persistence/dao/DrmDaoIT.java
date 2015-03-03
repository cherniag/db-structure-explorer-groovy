package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Drm;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class DrmDaoIT {

    @Resource(name = "persistence.DrmDao")
    private DrmDao drmDao;

    @Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
    public void testFindDrmTree_incorrectParam() throws Exception {
        int userId = 1;
        String isrc = null;

        drmDao.findDrmTree(userId, isrc);
    }

    @Test
    public void testFindDrmTree_success() throws Exception {
        int userId = 1;
        String isrc = "USAT21001886";

        List<Drm> drms = drmDao.findDrmTree(userId, isrc);
        Assert.assertNotNull(drms);
        Assert.assertEquals(1, drms.size());
    }
}