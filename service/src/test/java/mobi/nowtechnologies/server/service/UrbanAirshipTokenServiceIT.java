package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.UrbanAirshipToken;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * Created by enes on 1/27/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UrbanAirshipTokenServiceIT {

    @Resource
    private UrbanAirshipTokenRepository urbanAirshipTokenRepository;

    @Resource
    private UrbanAirshipTokenService urbanAirshipTokenService;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    UserStatusRepository userStatusRepository;

    @Test
    public void testNewTokenIsSavedProperly() {
        User user = createUser("USERNAME_1", "DEVICE_UID_1", "hl_uk");
        String urbanAirshipToken = "some-token-value";

        urbanAirshipTokenService.saveToken(user, urbanAirshipToken);

        UrbanAirshipToken token = urbanAirshipTokenRepository.findDataByUserId(user.getId());

        assertEquals(urbanAirshipToken, token.getToken());
        assertEquals(user.getId(), token.getUser().getId());
    }

    @Test
    public void testTokenIsUpdatesProperly() {
        User user = createUser("USERNAME_2", "DEVICE_UID_2", "hl_uk");
        String oldUrbanAirshipToken = "some-old-token-value";
        urbanAirshipTokenService.saveToken(user, oldUrbanAirshipToken);

        String newUrbanAirshipToken = "new-old-token-value";
        urbanAirshipTokenService.saveToken(user, newUrbanAirshipToken);

        UrbanAirshipToken token = urbanAirshipTokenRepository.findDataByUserId(user.getId());

        assertEquals(newUrbanAirshipToken, token.getToken());
        assertEquals(user.getId(), token.getUser().getId());
    }

    @Test
    public void testTokenIsMergedProperly() {
        User tempUser = createUser("USERNAME_TEMP", "DEVICE_UID_TEMP", "hl_uk");
        String tempToken = "some-token-for-temp-user";
        urbanAirshipTokenService.saveToken(tempUser, tempToken);

        User oldUser = createUser("USERNAME_OLD", "DEVICE_UID_OLD", "hl_uk");
        String oldToken = "some-token-for-old-user";
        urbanAirshipTokenService.saveToken(oldUser, oldToken);

        urbanAirshipTokenService.mergeToken(tempUser, oldUser);

        UrbanAirshipToken tempTokenFromDB = urbanAirshipTokenRepository.findDataByUserId(tempUser.getId());
        assertNull(tempTokenFromDB);

        UrbanAirshipToken oldTokenFromDB = urbanAirshipTokenRepository.findDataByUserId(oldUser.getId());
        assertNotNull(oldTokenFromDB);
        assertEquals(oldTokenFromDB.getToken(), tempToken);
        assertEquals(oldTokenFromDB.getUser().getId(), oldUser.getId());
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeCache.getAndroidDeviceType());
        user.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        user = userRepository.saveAndFlush(user);
        return user;
    }
}
