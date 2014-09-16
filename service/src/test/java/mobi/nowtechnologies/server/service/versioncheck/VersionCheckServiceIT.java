package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.ClientVersion;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionMessage;
import mobi.nowtechnologies.server.persistence.repository.VersionCheckRepository;
import mobi.nowtechnologies.server.persistence.repository.VersionMessageRepository;
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
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


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
    @Resource
    private VersionMessageRepository versionMessageRepository;
    @Resource
    private VersionCheckRepository versionCheckRepository;

    private Community community;

    private DeviceType deviceType;

    private static final String O2_TRACKS_APPLICATION_NAME = "O2_TRACKS";

    private static final String HL_UK_APPLICATION_NAME = "HL_UK_TRACKS";


    @Before
    public void prepareTest() {
        VersionMessage versionMessage1 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_REJECTED", "http://play.google.com/new_community_app"));
        VersionMessage versionMessage2 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app"));
        VersionMessage versionMessage3 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_SUGGESTED"));

        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage1,
                VersionCheckStatus.REVOKED,
                "O2_TRACKS",
                ClientVersion.from("1.5.0")
        ));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage2,
                VersionCheckStatus.FORCED_UPDATE,
                "O2_TRACKS",
                ClientVersion.from("1.6.0")
        ));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage3,
                VersionCheckStatus.SUGGESTED_UPDATE,
                "O2_TRACKS",
                ClientVersion.from("1.7.0")
        ));
    }

    @Test
    public void testVersionForO2CommunityWhereRangesArePresent() {
        community = CommunityDao.getCommunity("o2");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(1, 4, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 5, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 5, 1, VersionCheckStatus.FORCED_UPDATE, "VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app", O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 6, 0, VersionCheckStatus.FORCED_UPDATE, "VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app", O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 6, 1, VersionCheckStatus.SUGGESTED_UPDATE, "VERSION_SUGGESTED", null, O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 7, 0, VersionCheckStatus.SUGGESTED_UPDATE, "VERSION_SUGGESTED", null, O2_TRACKS_APPLICATION_NAME);
        checkVersion(1, 7, 1, VersionCheckStatus.CURRENT, null, null, O2_TRACKS_APPLICATION_NAME);
    }


    @Test
    public void testVersionForHLUKCommunityWhereNoConfigurationAtAll() {
        community = CommunityDao.getCommunity("hl_uk");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(1, 4, 0, VersionCheckStatus.CURRENT, null, null, HL_UK_APPLICATION_NAME);
        checkVersion(1, 7, 1, VersionCheckStatus.CURRENT, null, null, HL_UK_APPLICATION_NAME);
    }

    private void checkVersion(int major, int minor, int revision, VersionCheckStatus status, String messageKey, String url, String applicationName){
        VersionCheckResponse response = versionCheckService.check(buildRequest(major, minor, revision, applicationName));
        assertEquals(status, response.getStatus());
        if (StringUtils.isNotEmpty(messageKey)){
            assertEquals(response.getMessageKey(), messageKey);
        }
        if (StringUtils.isNotEmpty(url)){
            assertEquals(response.getUri(), url);
        }
    }

    private UserAgentRequest buildRequest(int major, int minor, int revision, String applicationName) {
        ClientVersion clientVersion = mock(ClientVersion.class);
        when(clientVersion.major()).thenReturn(major);
        when(clientVersion.minor()).thenReturn(minor);
        when(clientVersion.revision()).thenReturn(revision);
        UserAgentRequest userAgentRequest = mock(UserAgentRequest.class);
        when(userAgentRequest.getCommunity()).thenReturn(community);
        when(userAgentRequest.getPlatform()).thenReturn(deviceType);
        when(userAgentRequest.getVersion()).thenReturn(clientVersion);
        when(userAgentRequest.getApplicationName()).thenReturn(applicationName);
        return userAgentRequest;
    }
}

