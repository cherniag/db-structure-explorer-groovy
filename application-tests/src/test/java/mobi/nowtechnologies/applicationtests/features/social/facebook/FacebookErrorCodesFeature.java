package mobi.nowtechnologies.applicationtests.features.social.facebook;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.VersionTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Component
public class FacebookErrorCodesFeature {
    private String timestamp = new Date().getTime() + "";

    private UserDeviceData deviceData;

    @Resource
    private JsonHelper jsonHelper;

    @Resource
    MQAppClientDeviceSet flow;

    @Given("^First time user with device using (\\w+) format for ([\\w\\.]+) version and (\\w+) community and (\\w+) device$")
    public void given(RequestFormat format, @Transform(VersionTransformer.class) HasVersion version, String community, String deviceType) throws Throwable {
        deviceData = new UserDeviceData(version, community, deviceType);

        flow.setFormat(format);
    }

    @When("^User signs up the device$")
    public void userSignsUpTheDevice() throws Throwable {
        flow.singup(deviceData);
    }

    @Then("^Temporary registration info is available$")
    public void temporaryRegistrationInfoIsAvailable() throws Throwable {
        PhoneState phoneState = flow.getPhoneState(deviceData);
        assertFalse(phoneState.getAccountCheck().userToken.isEmpty());
    }

    @When("^User enters facebook info on his device and facebook returns empty email$")
    public void userEntersFacebookInfoOnHisDeviceAndFacebookReturnsEmptyEmail() throws Throwable {
        flow.loginUsingFacebookWithEmptyEmail(deviceData, timestamp);
    }

    @When("^User enters facebook info on his device and facebook returns the response with different id$")
    public void userEntersFacebookInfoOnHisDeviceAndFacebookReturnsResponseWithDifferentId() throws Throwable {
        flow.loginUsingFacebookWithDifferentId(deviceData, timestamp);
    }

    @When("^User enters facebook info on his device and facebook returns the response with invalid access token$")
    public void userEntersFacebookInfoOnHisDeviceAndFacebookReturnsResponseWithInvalidAccesstoken() throws Throwable {
        flow.loginUsingFacebookWithInvalidAccessToken(deviceData, timestamp);
    }

    @Then("^User gets (\\d+) http error code and (\\d+) error code and (.*) message$")
    public void userGetsError(final int httpErrorCode, final int errorCode, final String errorBody) throws Throwable {
        // check the http status code
        HttpClientErrorException lastFacebookError = flow.getPhoneState(deviceData).getLastFacebookError();
        assertEquals(httpErrorCode, lastFacebookError.getStatusCode().value());

        // check the message and the code
        ErrorMessage errorMessage = jsonHelper.extractObjectValueByPath(lastFacebookError, JsonHelper.ERROR_MESSAGE_PATH, ErrorMessage.class);
        assertEquals(errorCode, errorMessage.getErrorCode().intValue());
        assertEquals(errorBody, errorMessage.getMessage());

    }

}
