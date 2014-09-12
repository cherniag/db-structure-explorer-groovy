package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Oleg Artomov on 9/11/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class VersionCheckServiceIT {

    @Resource
    private VersionCheckService versionCheckService;

    private Community community;

    private DeviceType deviceType;


    @Before
    public void prepareTest() {
    }

    @Test
    public void testVersionForO2CommunityWhereRangesArePresent() {
        community = CommunityDao.getCommunity("o2");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(1, 4, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app");
        checkVersion(1, 5, 1, VersionCheckStatus.FORCED_UPDATE, "VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app");
        checkVersion(1, 7, 0, VersionCheckStatus.SUGGESTED_UPDATE, "VERSION_SUGGESTED", null);
        checkVersion(1, 7, 1, VersionCheckStatus.CURRENT, null, null);
    }


    @Test
    public void testVersionForHLUKCommunityWhereNoConfigurationAtAll() {
        community = CommunityDao.getCommunity("hl_uk");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(1, 4, 0, VersionCheckStatus.CURRENT, null, null);
        checkVersion(1, 7, 1, VersionCheckStatus.CURRENT, null, null);
    }

    private void checkVersion(int major, int minor, int revision, VersionCheckStatus status, String messageKey, String url){
        VersionCheckResponse response = versionCheckService.check(buildRequest(major, minor, revision));
        assertEquals(response.getStatus(), status);
        if (StringUtils.isNotEmpty(messageKey)){
            assertEquals(response.getMessageKey(), messageKey);
        }
        if (StringUtils.isNotEmpty(url)){
            assertEquals(response.getUri().toString(), url);
        }
    }

    private UserAgentRequest buildRequest(int major, int minor, int revision) {
        UserAgentRequestImpl request = new UserAgentRequestImpl();
        request.setCommunity(community);
        request.setPlatform(deviceType);
        request.setVersion(new ClientVersionImpl(major, minor, revision));
        return request;
    }
}

