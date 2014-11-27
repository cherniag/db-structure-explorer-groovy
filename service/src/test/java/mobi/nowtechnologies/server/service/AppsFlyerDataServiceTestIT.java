package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AppsFlyerData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.AppsFlyerDataRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" })
public class AppsFlyerDataServiceTestIT {

    @Resource
    private AppsFlyerDataService appsFlyerDataService;

    @Resource
    private AppsFlyerDataRepository appsFlyerDataRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private UserRepository userRepository;

    @Test
    public void saveAppsFlyerData() throws Exception {
        User user = createUser("USERNAME_1", "DEVICE_UID_1", "hl_uk");

        appsFlyerDataService.saveAppsFlyerData(user, "APPS-F-ID");

        AppsFlyerData found = appsFlyerDataRepository.findDataByUserId(user.getId());
        assertEquals("APPS-F-ID", found.getAppsFlyerUid());
        assertEquals(user.getId(), found.getUserId());
    }

    @Test
    public void saveAppsFlyerDataForTheSameUser() throws Exception {
        User user = createUser("USERNAME_2", "DEVICE_UID_2", "hl_uk");
        appsFlyerDataService.saveAppsFlyerData(user, "APPS-F-ID1");
        appsFlyerDataService.saveAppsFlyerData(user, "APPS-F-ID2");

        AppsFlyerData found = appsFlyerDataRepository.findDataByUserId(user.getId());
        assertEquals("APPS-F-ID2", found.getAppsFlyerUid());
    }

    @Test
    public void mergeExistingFromDataIntoExistingToData() throws Exception {
        User fromUser = createUser("USERNAME-TEMP", "DEVICE-TEMP", "hl_uk");
        User toUser = createUser("USERNAME-OLD", "DEVICE-OLD", "hl_uk");

        createAppsFlyerData(fromUser, "FROM-F-ID");
        createAppsFlyerData(toUser, "TO-F-ID");

        appsFlyerDataService.mergeAppsFlyerData(fromUser, toUser);

        AppsFlyerData foundFrom = appsFlyerDataRepository.findDataByUserId(fromUser.getId());
        assertNull(foundFrom);
        AppsFlyerData foundTo = appsFlyerDataRepository.findDataByUserId(toUser.getId());
        assertEquals("FROM-F-ID", foundTo.getAppsFlyerUid());
        assertEquals(toUser.getId(), foundTo.getUserId());
    }

    private void createAppsFlyerData(User user, String appsFlyerUid) {
        AppsFlyerData appsFlyerData = new AppsFlyerData(user.getId(), appsFlyerUid);
        appsFlyerDataRepository.saveAndFlush(appsFlyerData);
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(UserStatusDao.getSubscribedUserStatus());
        user = userRepository.saveAndFlush(user);
        return user;
    }
}