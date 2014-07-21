package mobi.nowtechnologies.applicationtests.features;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.DeviceTypesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.VersionTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Component
public class XtifyOnSignupFeature {
    @Resource
    private PartnerDeviceSet partnerDeviceSet;
    @Resource
    private UserDeviceDataService userDeviceDataService;
    @Resource
    private UserDbService userDbService;
    @Resource
    private DeviceUserDataRepository deviceUserDataRepository;

    private List<UserDeviceData> userDeviceDatas;

    @Given("^First time user with device using (\\w+) format for ([\\w\\.]+) version and (\\w+) and (\\w+) communities and for (\\w+) devices available$")
    public void firstTimeUser(RequestFormat requestFormat,
                                @Transform(VersionTransformer.class) HasVersion version,
                                String o2,
                                String vf_nz,
                                @Transform(DeviceTypesTransformer.class) List<String> deviceTypes) {
        partnerDeviceSet.setFormat(requestFormat);
        userDeviceDatas = userDeviceDataService.table(version, Arrays.asList(o2, vf_nz), deviceTypes);
    }

    @When("^User registers using device with token$")
    public void whenUserRegistersWithToken() {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            String xtify = UUID.randomUUID().toString();
            partnerDeviceSet.singup(userDeviceData, xtify);
        }
    }

    @When("^User registers using device sending empty xtify token$")
    public void whenUserRegistersWithNoToken() {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            String emptyToken = StringUtils.EMPTY;
            partnerDeviceSet.singup(userDeviceData, emptyToken);
        }
    }

    @Then("^User should have (\\w+) activation status in database")
    public void thenUserShouldRegistered(ActivationStatus activationStatus) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = findUserInDatabase(userDeviceData, phoneState);
            assertEquals(activationStatus, user.getActivationStatus());
        }
    }

    @And("^device user data should not be created$")
    public void deviceDataShouldNotBeCreated() {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = findUserInDatabase(userDeviceData, phoneState);

            DeviceUserData deviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
            assertNull(deviceUserData);
        }
    }

    @And("^device user data should be created with xtify user sent$")
    public void deviceDataShouldBeCreatedWithXtifyUserSent() {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = findUserInDatabase(userDeviceData, phoneState);

            DeviceUserData deviceUserData = deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
            assertNotNull(deviceUserData);
            assertEquals(phoneState.getLastSentXTofyToken(), deviceUserData.getXtifyToken());
        }
    }

    @After
    public void cleanDevicesSet() {
        partnerDeviceSet.cleanup();
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), userDeviceData.getCommunityUrl());
    }

}
