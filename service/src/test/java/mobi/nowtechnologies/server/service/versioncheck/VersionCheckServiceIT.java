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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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
    private static final String VF_NZ_TRACKS_APPLICATION_NAME = "VF_NZ_TRACKS";
    private static final String O2_TRACKS_APPLICATION_NAME_WQ = "O2_TRACKS_WQ";

    private static final String HL_UK_APPLICATION_NAME = "HL_UK_TRACKS";


    @Before
    public void prepareTest() {
        VersionMessage versionMessage1 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_REJECTED", "http://play.google.com/new_community_app"));
        VersionMessage versionMessage2 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app"));
        VersionMessage versionMessage3 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_SUGGESTED"));
        VersionMessage versionMessage4 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_REJECTED"));
        VersionMessage versionMessage5 = versionMessageRepository.saveAndFlush(new VersionMessage("VERSION_MIGRATED", "http://play.google.com/new_community"));

        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage1,
                VersionCheckStatus.REVOKED,
                O2_TRACKS_APPLICATION_NAME,
                ClientVersion.from("1.5.0"),
                "image_revoked_1.5.0.jpg"));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage4,
                VersionCheckStatus.REVOKED,
                O2_TRACKS_APPLICATION_NAME,
                ClientVersion.from("1.5.5"),
                null));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage4,
                VersionCheckStatus.MIGRATED,
                O2_TRACKS_APPLICATION_NAME,
                ClientVersion.from("1.5.9"),
                null));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage1,
                VersionCheckStatus.REVOKED,
                O2_TRACKS_APPLICATION_NAME_WQ,
                ClientVersion.from("1.5.0-RELEASE"),
                "image_revoked_1.5.0-RELEASE.jpg"));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage2,
                VersionCheckStatus.FORCED_UPDATE,
                O2_TRACKS_APPLICATION_NAME,
                ClientVersion.from("1.6.0"),
                "image_forced_1.6.0.jpg"));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("o2"),
                versionMessage3,
                VersionCheckStatus.SUGGESTED_UPDATE,
                O2_TRACKS_APPLICATION_NAME,
                ClientVersion.from("1.7.0"),
                "image_suggested_1.7.0.jpg"));
        versionCheckRepository.saveAndFlush(new VersionCheck(
                DeviceTypeDao.getAndroidDeviceType(),
                CommunityDao.getCommunity("vf_nz"),
                versionMessage5,
                VersionCheckStatus.MIGRATED,
                VF_NZ_TRACKS_APPLICATION_NAME,
                ClientVersion.from("2.0.0"),
                "image_migrated_2.0.0.jpg"));
    }

    @Test
    public void testVersionForO2CommunityWhereRangesArePresent() {
        community = CommunityDao.getCommunity("o2");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(null, 0, 0, 1, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME, "image_revoked_1.5.0.jpg", false);
        checkVersion(null, 1, 0, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME, "image_revoked_1.5.0.jpg", false);
        checkVersion(null, 1, 4, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME, "image_revoked_1.5.0.jpg", false);
        checkVersion(null, 1, 4, 9, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME, "image_revoked_1.5.0.jpg", false);
        checkVersion(null, 1, 5, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME, "image_revoked_1.5.0.jpg", false);
        checkVersion(null, 1, 5, 3, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "", O2_TRACKS_APPLICATION_NAME, null, false);
        checkVersion(null, 1, 5, 5, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "", O2_TRACKS_APPLICATION_NAME, null, false);
        checkVersion(null, 1, 5, 8, VersionCheckStatus.FORCED_UPDATE, "VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app", O2_TRACKS_APPLICATION_NAME, "image_forced_1.6.0.jpg", false);
        checkVersion(null, 1, 6, 0, VersionCheckStatus.FORCED_UPDATE, "VERSION_FORCED_UPGRADE", "http://play.google.com/new_version_app", O2_TRACKS_APPLICATION_NAME, "image_forced_1.6.0.jpg", false);
        checkVersion(null, 1, 6, 1, VersionCheckStatus.SUGGESTED_UPDATE, "VERSION_SUGGESTED", null, O2_TRACKS_APPLICATION_NAME, "image_suggested_1.7.0.jpg", false);
        checkVersion(null, 1, 7, 0, VersionCheckStatus.SUGGESTED_UPDATE, "VERSION_SUGGESTED", null, O2_TRACKS_APPLICATION_NAME, "image_suggested_1.7.0.jpg", false);
        checkVersion(null, 1, 7, 1, VersionCheckStatus.CURRENT, null, null, O2_TRACKS_APPLICATION_NAME, null, false);
        checkVersion(null, 2, 0, 1, VersionCheckStatus.CURRENT, null, null, O2_TRACKS_APPLICATION_NAME, null, false);
    }

    @Test
    public void testVersionForO2CommunityWhereRangesArePresentForQualifier() {
        community = CommunityDao.getCommunity("o2");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion("RELEASE", 1, 5, 0, VersionCheckStatus.REVOKED, "VERSION_REJECTED", "http://play.google.com/new_community_app", O2_TRACKS_APPLICATION_NAME_WQ, "image_revoked_1.5.0-RELEASE.jpg", false);
    }


    @Test
    public void testVersionForHLUKCommunityWhereNoConfigurationAtAll() {
        community = CommunityDao.getCommunity("hl_uk");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(null, 1, 4, 0, VersionCheckStatus.CURRENT, null, null, HL_UK_APPLICATION_NAME, null, false);
        checkVersion(null, 1, 7, 1, VersionCheckStatus.CURRENT, null, null, HL_UK_APPLICATION_NAME, null, false);
    }

    @Test
    public void checkMigratedVersionStatus() {
        community = CommunityDao.getCommunity("vf_nz");
        deviceType = DeviceTypeDao.getAndroidDeviceType();
        checkVersion(null, 1, 5, 0, VersionCheckStatus.MIGRATED, "VERSION_MIGRATED", "http://play.google.com/new_community", VF_NZ_TRACKS_APPLICATION_NAME, "image_migrated_2.0.0.jpg", true);
        checkVersion(null, 2, 0, 0, VersionCheckStatus.MIGRATED, "VERSION_MIGRATED", "http://play.google.com/new_community", VF_NZ_TRACKS_APPLICATION_NAME, "image_migrated_2.0.0.jpg", true);
        checkVersion(null, 2, 5, 0, VersionCheckStatus.CURRENT, null, null, VF_NZ_TRACKS_APPLICATION_NAME, null, true);

        //without migrated status
        checkVersion(null, 1, 5, 0, VersionCheckStatus.CURRENT, null, null, VF_NZ_TRACKS_APPLICATION_NAME, null, false);
        checkVersion(null, 2, 0, 0, VersionCheckStatus.CURRENT, null, null, VF_NZ_TRACKS_APPLICATION_NAME, null, false);
    }

    private void checkVersion(String qualifier, int major, int minor, int revision, VersionCheckStatus status, String messageKey, String url, String applicationName, String imageFileName, boolean canHaveMigratedStatus){
        Set<VersionCheckStatus> includedStatuses = canHaveMigratedStatus ? VersionCheckStatus.getAllStatuses() : VersionCheckStatus.getAllStatusesWithoutMigrated();
        VersionCheckResponse response = versionCheckService.check(buildRequest(qualifier, major, minor, revision, applicationName), includedStatuses);
        assertEquals(status, response.getStatus());
        assertEquals(messageKey, response.getMessageKey());
        assertEquals(imageFileName, response.getImageFileName());

        if(StringUtils.isEmpty(url)){
            assertTrue(StringUtils.isEmpty(response.getUri()));
        }else{
            assertEquals(url, response.getUri());
        }
    }

    private UserAgentRequest buildRequest(String qualifier, int major, int minor, int revision, String applicationName) {
        ClientVersion clientVersion = mock(ClientVersion.class);
        when(clientVersion.major()).thenReturn(major);
        when(clientVersion.minor()).thenReturn(minor);
        when(clientVersion.revision()).thenReturn(revision);
        if(qualifier != null) {
            when(clientVersion.qualifier()).thenReturn(qualifier);
        }

        UserAgentRequest userAgentRequest = mock(UserAgentRequest.class);
        when(userAgentRequest.getCommunity()).thenReturn(community);
        when(userAgentRequest.getPlatform()).thenReturn(deviceType);
        when(userAgentRequest.getVersion()).thenReturn(clientVersion);
        when(userAgentRequest.getApplicationName()).thenReturn(applicationName);
        return userAgentRequest;
    }
}

