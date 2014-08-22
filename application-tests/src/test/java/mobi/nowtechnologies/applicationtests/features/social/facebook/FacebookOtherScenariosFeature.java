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
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@Component
public class FacebookOtherScenariosFeature {
    List<UserDeviceData> currentUserDevices;

    @Resource
    JsonHelper jsonHelper;
    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    UserDbService userDbService;
    @Resource
    FacebookUserInfoRepository fbDetailsRepository;

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

    @When("^User signs up the device and enters facebook info on his device$")
    public void userSignUpTheDeviceAndEntersFacebookInfoOnHisDevice() {
        for (UserDeviceData deviceData : currentUserDevices) {
            deviceSet.singup(deviceData);
            deviceSet.loginUsingFacebook(deviceData);
        }
    }

    @Then("^User is successfully registered and the promo is applied$")
    public void userIsSuccessfullyRegisteredAndThePromoIsApplied() throws Throwable {
        checkFacebookUserEmailsAndAccountId();
    }

    @When("^User signs in the with different device using same account$")
    public void userSignsUpSecondTimeWithDifferentDevice() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            deviceSet.changePhone(deviceData);
            deviceSet.singup(deviceData);
            deviceSet.loginUsingFacebook(deviceData);
        }
    }

    @Then("^user account info is not changed$")
    public void userAccountIsMerged() {
        checkFacebookUserEmailsAndAccountId();
    }

    @When("^User signs in the with different account using same device$")
    public void userSignsUpSecondTimeWithDifferentAccount() throws Throwable {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = deviceSet.getPhoneState(deviceData);

            deviceSet.changePhone(deviceData);
            deviceSet.singup(deviceData);

            final String differentEmail = "diff." + phoneState.getEmail();
            final String differentAccountId = System.nanoTime() + "";
            deviceSet.loginUsingFacebookWithDefinedAccountIdAndEmail(deviceData, differentEmail, differentAccountId);
        }
    }

    @Then("^user account info is updated$")
    public void userAccountInfoIsUpdated() {
        checkFacebookUserEmailsAndAccountId();
    }

    private void checkFacebookUserEmailsAndAccountId() {
        for (UserDeviceData deviceData : currentUserDevices) {
            PhoneState phoneState = deviceSet.getPhoneState(deviceData);

            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), deviceData.getCommunityUrl());
            FacebookUserInfo facebookUserInfo = fbDetailsRepository.findByUser(user);

            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail());
            assertEquals(facebookUserInfo.getFacebookId(), phoneState.getFacebookUserId());
        }
    }


}
