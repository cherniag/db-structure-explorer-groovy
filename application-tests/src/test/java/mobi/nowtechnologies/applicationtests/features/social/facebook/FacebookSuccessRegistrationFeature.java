package mobi.nowtechnologies.applicationtests.features.social.facebook;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.DeviceTypesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.social.facebook.transformers.CommunityTransformer;
import mobi.nowtechnologies.applicationtests.features.social.facebook.transformers.VersionsTransformer;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Component
public class FacebookSuccessRegistrationFeature {
    @Resource
    private UserDataCreator userDataCreator;
    @Resource
    private UserDbService userDbService;

    @Resource
    private UserDeviceDataService userDeviceDataService;

    @Resource
    private FacebookUserInfoRepository fbDetailsRepository;

    //
    // Variables
    //
    private String timestamp = new Date().getTime() + "";

    private List<UserDeviceData> currentUserDevices;

    @Resource
    MQAppClientDeviceSet json;
    @Resource
    MQAppClientDeviceSet xml;
    @Resource
    MQAppClientDeviceSet city;

    MQAppClientDeviceSet currentFlow;

    @Given("^First time user with device using (\\w+) format for all facebook (\\w+) and facebook (\\w+) and all (\\w+) available$")
    public void firstTimeUserUsingJsonAndXmlFormats(RequestFormat format,
                                                    @Transform(VersionsTransformer.class) List<HasVersion> versions,
                                                    @Transform(CommunityTransformer.class) List<String> communities,
                                                    @Transform(DeviceTypesTransformer.class) List<String> devices) throws Throwable {
        if(format == RequestFormat.XML) {
            xml.setFormat(RequestFormat.XML);
            currentFlow = xml;
        } else {
            currentFlow = json;
        }

        currentUserDevices = userDeviceDataService.table(versions, communities, devices);
    }

    @Given("^First time user with device using JSON format and Facebook returns only City location value in response for all facebook (\\w+) and facebook (\\w+) and all (\\w+) available$")
    public void firstTimeUserUsingJsonFormatAndFacebookReturnsOnlyCityLocationInResponse(@Transform(VersionsTransformer.class) List<HasVersion> versions,
                                                                                         @Transform(CommunityTransformer.class) List<String> communities,
                                                                                         @Transform(DeviceTypesTransformer.class) List<String> devices) throws Throwable {
        currentFlow = city;
        currentUserDevices = userDeviceDataService.table(versions, communities, devices);
    }

    @When("^User signs up the device$")
    public void userSignsUpTheDevice() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            currentFlow.singup(deviceData);
        }
    }

    @Then("^Temporary registration info is available$")
    public void temporaryRegistrationInfoIsAvailable() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = currentFlow.getPhoneState(deviceData);
            assertFalse(phoneState.getAccountCheck().userToken.isEmpty());
        }
    }

    @When("^User enters facebook info on his device$")
    public void userEntersFacebookInfoOnHisDevice() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            if(currentFlow == city) {
                currentFlow.loginUsingFacebookWithCityOnly(deviceData, timestamp);
            } else {
                currentFlow.loginUsingFacebook(deviceData, timestamp);
            }
        }
    }

    @Then("^User is successfully registered and the promo is applied$")
    public void userIsSuccessfullyRegisteredAndThePromoIsApplied() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = currentFlow.getPhoneState(deviceData);

            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), deviceData.getCommunityUrl());
            FacebookUserInfo facebookUserInfo = fbDetailsRepository.findByUser(user);

            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail());
        }
    }

    @When("^User tries to get chart$")
    public void userTriesToGetChart() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = currentFlow.getPhoneState(deviceData);

            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), deviceData.getCommunityUrl());
            String userToken = userDataCreator.createUserToken(phoneState.getAccountCheck(), timestamp);

            currentFlow.getChart(deviceData, user.getUserName(), timestamp, userToken);
        }
    }

    @Then("^it gets response successfully$")
    public void itGetsItSuccessfully() throws Throwable {
    }

}
