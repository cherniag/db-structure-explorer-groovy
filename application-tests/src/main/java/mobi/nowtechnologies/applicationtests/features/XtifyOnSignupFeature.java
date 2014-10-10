package mobi.nowtechnologies.applicationtests.features;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
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

import static org.junit.Assert.*;

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

    @Given("^First time user with device using JSON and XML formats for ([\\w\\.]+) version and (.+) and for (.+) available$")
    public void firstTimeUser(String version,
                                @Transform(DictionaryTransformer.class) Word communities,
                                @Transform(DictionaryTransformer.class) Word devices) {
        userDeviceDatas = userDeviceDataService.table(Arrays.asList(version), communities.set(), devices.set());
    }

    @Given("^First time user with device using JSON and XML formats for (.+) above (.+) and (.+) and for (.+) available$")
    public void firstTimeUserAbove(@Transform(DictionaryTransformer.class) Word versions,
                              String aboveVersion,
                              @Transform(DictionaryTransformer.class) Word communities,
                              @Transform(DictionaryTransformer.class) Word devices) {
        List<String> above = ApiVersions.from(versions.set()).above(aboveVersion);
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), devices.set());
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
        return userDbService.findUser(phoneState, userDeviceData);
    }

}
