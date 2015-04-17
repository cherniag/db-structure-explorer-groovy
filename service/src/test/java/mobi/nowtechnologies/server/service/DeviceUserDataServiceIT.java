package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import javax.annotation.Resource;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
public class DeviceUserDataServiceIT {

    @Resource
    private DeviceUserDataService deviceUserDataService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private DeviceUserDataRepository deviceUserDataRepository;

    @Before
    public void setUp() throws Exception {
        deviceUserDataRepository.deleteAll();
    }

    @Test
    public void checkSaveNewToken() throws Exception {
        User user = userRepository.findOne(102);
        deviceUserDataService.saveXtifyToken(user, "x1");
        DeviceUserData found = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        assertThat(found.getXtifyToken(), is("x1"));
        assertThat(found.getUserId(), is(user.getId()));
        assertThat(found.getDeviceUid(), is(user.getDeviceUID()));
        assertThat(found.getCommunityUrl(), is(user.getCommunityRewriteUrl()));
    }

    @Test
    public void checkSaveNewTokenForExistingData() throws Exception {
        //save token for user
        User user = userRepository.findOne(102);
        deviceUserDataService.saveXtifyToken(user, "x1");
        DeviceUserData oldDeviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        assertThat(oldDeviceUserData.getXtifyToken(), is("x1"));
        //save new token for the same user
        deviceUserDataService.saveXtifyToken(user, "x2");
        DeviceUserData newDeviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        assertThat(newDeviceUserData.getXtifyToken(), is("x2"));
        assertThat(deviceUserDataRepository.findByXtifyToken("x1"), nullValue());
    }

    @Test
    public void checkSaveSameTokenForAnotherUser() throws Exception {
        //save token for user
        User oldUser = userRepository.findOne(102);
        String token = "x1";
        deviceUserDataService.saveXtifyToken(oldUser, token);
        DeviceUserData oldDeviceUserData = deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID());
        assertThat(oldDeviceUserData.getXtifyToken(), is(token));
        //save the same token for another user
        User newUser = userRepository.findOne(103);
        deviceUserDataService.saveXtifyToken(newUser, token);
        DeviceUserData newDeviceUserData = deviceUserDataRepository.find(newUser.getId(), newUser.getCommunityRewriteUrl(), newUser.getDeviceUID());
        assertThat(newDeviceUserData.getXtifyToken(), is(token));
        assertThat(deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID()), nullValue());
    }

    @Test
    public void checkUpdateTokenAfterMerge() throws Exception {
        //store old token for old user
        User oldUser = userRepository.findOne(102);
        String oldToken = "x1";
        deviceUserDataService.saveXtifyToken(oldUser, oldToken);
        DeviceUserData oldDeviceUserData = deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID());
        assertThat(oldDeviceUserData.getXtifyToken(), is(oldToken));
        //store new token for temp user record with the same deviceUID
        User tempUser = userRepository.findOne(103);
        String newToken = "x2";
        deviceUserDataService.saveXtifyToken(tempUser, newToken);
        DeviceUserData newDeviceUserData = deviceUserDataRepository.find(tempUser.getId(), tempUser.getCommunityRewriteUrl(), tempUser.getDeviceUID());
        assertThat(newDeviceUserData.getXtifyToken(), is(newToken));
        //store new token for old user after merge
        deviceUserDataService.saveXtifyToken(oldUser, newToken);
        assertThat(deviceUserDataRepository.find(tempUser.getId(), tempUser.getCommunityRewriteUrl(), tempUser.getDeviceUID()), nullValue());
        DeviceUserData data = deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID());
        assertThat(data, notNullValue());
        assertThat(data.getXtifyToken(), is(newToken));
    }

}