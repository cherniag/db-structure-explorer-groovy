package mobi.nowtechnologies.applicationtests.features.social.facebook;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.DeviceTypesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.social.facebook.transformers.CommunityTransformer;
import mobi.nowtechnologies.applicationtests.features.social.facebook.transformers.VersionsTransformer;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Component
public class FacebookSuccessRegistrationFeature {
    @Resource
    private UserDbService userDbService;

    @Resource
    private UserDeviceDataService userDeviceDataService;

    @Resource
    private FacebookUserInfoRepository fbDetailsRepository;

    //
    // Variables
    //
    private boolean city;
    private List<UserDeviceData> currentUserDevices;

    @Resource
    MQAppClientDeviceSet deviceSet;

    @Given("^First time user with device using (\\w+) format for all facebook (\\w+) and facebook (\\w+) and all (\\w+) available$")
    public void firstTimeUserUsingJsonAndXmlFormats(RequestFormat format,
                                                    @Transform(VersionsTransformer.class) List<HasVersion> versions,
                                                    @Transform(CommunityTransformer.class) List<String> communities,
                                                    @Transform(DeviceTypesTransformer.class) List<String> devices) throws Throwable {
        deviceSet.setFormat(format);
        currentUserDevices = userDeviceDataService.table(versions, communities, devices);
    }

    @Given("^First time user with device using JSON format and Facebook returns only City location value in response for all facebook (\\w+) and facebook (\\w+) and all (\\w+) available$")
    public void firstTimeUserUsingJsonFormatAndFacebookReturnsOnlyCityLocationInResponse(@Transform(VersionsTransformer.class) List<HasVersion> versions,
                                                                                         @Transform(CommunityTransformer.class) List<String> communities,
                                                                                         @Transform(DeviceTypesTransformer.class) List<String> devices) throws Throwable {
        city = true;
        currentUserDevices = userDeviceDataService.table(versions, communities, devices);
    }

    @When("^User signs up the device$")
    public void userSignsUpTheDevice() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            deviceSet.singup(deviceData);
        }
    }

    @Then("^Temporary registration info is available$")
    public void temporaryRegistrationInfoIsAvailable() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = deviceSet.getPhoneState(deviceData);
            assertFalse(phoneState.getLastAccountCheckResponse().userToken.isEmpty());
        }
    }

    @When("^User enters facebook info on his device$")
    public void userEntersFacebookInfoOnHisDevice() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            if(city) {
                deviceSet.loginUsingFacebookWithCityOnly(deviceData);
            } else {
                deviceSet.loginUsingFacebook(deviceData);
            }
        }
        if(city) {
            city = false;
        }
    }

    @Then("^User is successfully registered and the promo is applied$")
    public void userIsSuccessfullyRegisteredAndThePromoIsApplied() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = deviceSet.getPhoneState(deviceData);

            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), deviceData.getCommunityUrl());
            FacebookUserInfo facebookUserInfo = fbDetailsRepository.findByUser(user);

            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail());
        }
    }

    @When("^User tries to get chart$")
    public void userTriesToGetChart() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = deviceSet.getPhoneState(deviceData);

            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), deviceData.getCommunityUrl());

            deviceSet.getChart(deviceData, user.getUserName());
        }
    }

    @Then("^it gets response successfully$")
    public void itGetsItSuccessfully() throws Throwable {
    }

    @After
    public void cleanup() {
        deviceSet.cleanup();
    }

}
