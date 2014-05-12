package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/service-test.xml", "classpath:/META-INF/dao-test.xml","/META-INF/shared.xml"})
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
        User user = userRepository.findOne(102);
        deviceUserDataService.saveXtifyToken(user, "x1");
        DeviceUserData oldDeviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        assertThat(oldDeviceUserData.getXtifyToken(), is("x1"));
        deviceUserDataService.saveXtifyToken(user, "x2");
        DeviceUserData newDeviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        assertThat(newDeviceUserData.getXtifyToken(), is("x2"));
        assertThat(deviceUserDataRepository.findByXtifyToken("x1"), nullValue());
    }

    @Test
    public void checkSaveSameTokenForAnotherUser() throws Exception {
        User oldUser = userRepository.findOne(102);
        String token = "x1";
        deviceUserDataService.saveXtifyToken(oldUser, token);
        DeviceUserData oldDeviceUserData = deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID());
        assertThat(oldDeviceUserData.getXtifyToken(), is(token));
        User newUser = userRepository.findOne(103);
        deviceUserDataService.saveXtifyToken(newUser, token);
        DeviceUserData newDeviceUserData = deviceUserDataRepository.find(newUser.getId(), newUser.getCommunityRewriteUrl(), newUser.getDeviceUID());
        assertThat(newDeviceUserData.getXtifyToken(), is(token));
        assertThat(deviceUserDataRepository.find(oldUser.getId(), oldUser.getCommunityRewriteUrl(), oldUser.getDeviceUID()), nullValue());
    }

}