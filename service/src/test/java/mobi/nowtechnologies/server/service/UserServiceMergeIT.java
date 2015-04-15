package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;

import javax.annotation.Resource;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
public class UserServiceMergeIT {

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private DeviceUserDataRepository deviceUserDataRepository;

    @Resource
    private UserRepository userRepository;

    @Test
    public void testRemoveDeviceUserDataWhenMergeSameDevice() throws Exception {
        //disable old user
        User oldUser = userRepository.findOne(102);
        String deviceUID = oldUser.getDeviceUID();
        oldUser.setDeviceUID(deviceUID + "disabled_at");
        oldUser.setUuid(Utils.getRandomUUID());
        oldUser = userRepository.saveAndFlush(oldUser);
        deviceUserDataRepository.saveAndFlush(new DeviceUserData(oldUser.getCommunityRewriteUrl(), oldUser.getId(), deviceUID, "x1"));
        //register temp user
        User tempUser = new User();
        String tempUUID = Utils.getRandomUUID();
        tempUser.setUuid(tempUUID);
        tempUser.setUserName(deviceUID);
        tempUser.setMobile(oldUser.getMobile());
        tempUser.setUserGroup(oldUser.getUserGroup());
        tempUser.setDeviceUID(deviceUID);
        tempUser.setDeviceType(oldUser.getDeviceType());
        tempUser.setDeviceModel(oldUser.getDeviceModel());
        tempUser.setIpAddress(oldUser.getIpAddress());
        tempUser = userRepository.saveAndFlush(tempUser);
        deviceUserDataRepository.saveAndFlush(new DeviceUserData(tempUser, "x2"));
        //merge
        userService.mergeUser(oldUser, tempUser);

        User temp = userRepository.findOne(tempUser.getId());
        assertNull(temp);
        User old = userRepository.findOne(oldUser.getId());
        assertEquals(deviceUID, old.getDeviceUID());
        assertEquals(tempUUID, old.getUuid());
        DeviceUserData deviceUserDataForTempUser = deviceUserDataRepository.find(tempUser.getId(), tempUser.getCommunityRewriteUrl(), tempUser.getDeviceUID());
        assertNull(deviceUserDataForTempUser);
    }

    @Test
    public void testRemoveDeviceUserDataWhenMergeAnotherDevice() throws Exception {
        User oldUser = userRepository.findOne(103);
        deviceUserDataRepository.saveAndFlush(new DeviceUserData(oldUser, "x1"));
        //register temp user
        String anotherDeviceUID = "ddd2";
        User tempUser = new User();
        tempUser.setUserName(anotherDeviceUID);
        tempUser.setMobile(oldUser.getMobile());
        tempUser.setUserGroup(oldUser.getUserGroup());
        tempUser.setDeviceUID(anotherDeviceUID);
        tempUser.setDeviceType(oldUser.getDeviceType());
        tempUser.setDeviceModel(oldUser.getDeviceModel());
        tempUser.setIpAddress(oldUser.getIpAddress());
        tempUser = userRepository.saveAndFlush(tempUser);
        deviceUserDataRepository.saveAndFlush(new DeviceUserData(tempUser, "x2"));
        //merge
        userService.mergeUser(oldUser, tempUser);

        User temp = userRepository.findOne(tempUser.getId());
        assertNull(temp);
        User old = userRepository.findOne(oldUser.getId());
        assertEquals(anotherDeviceUID, old.getDeviceUID());
        DeviceUserData deviceUserDataForTempUser = deviceUserDataRepository.find(tempUser.getId(), tempUser.getCommunityRewriteUrl(), tempUser.getDeviceUID());
        assertNull(deviceUserDataForTempUser);
        DeviceUserData deviceUserDataForOldUser = deviceUserDataRepository.find(oldUser.getId(), tempUser.getCommunityRewriteUrl(), anotherDeviceUID);
        assertNull(deviceUserDataForOldUser);
    }

}