package mobi.nowtechnologies.applicationtests.features.streamzine;

import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.common.standard.StandardResponse;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Component
public class GetStreamzineNotSupportedCommunityFeature extends AbstractStreamzineFeature {

    private Map<UserDeviceData, ResponseEntity<StandardResponse>> errorResponses = new HashMap<UserDeviceData, ResponseEntity<StandardResponse>>();

    private Map<UserDeviceData, Update> updates = new HashMap<UserDeviceData, Update>();

    private String validResolution = "400x400";
    private ApiVersions apiVersions;

    //
    // Given and After
    //
    @Given("^First time user with device using (.+) format for (.+) and (.+) and for (.+) available$")
    public void firstTimeUserUsingFormat(@Transform(DictionaryTransformer.class) Word requestFormats, @Transform(DictionaryTransformer.class) Word versions,
                                         @Transform(DictionaryTransformer.class) Word communities, @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        apiVersions = ApiVersions.from(versions.list());
        currentUserDevices = super.initUserData(requestFormats.set(RequestFormat.class), versions, communities, devices);
    }

    @After
    public void cleanDevicesSet() {
        errorResponses.clear();
    }

    //
    // Successful Scenario
    //
    @When("^update is prepared$")
    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public void updateIsPrepared() {
        for (UserDeviceData data : currentUserDevices) {
            Community c = communityRepository.findByRewriteUrlParameter(data.getCommunityUrl());
            updates.put(data, new Update(DateUtils.addMilliseconds(new Date(), 100), c));
        }
    }

    //
    // Incorrect community scenario
    //
    @When("^user invokes get streamzine command with incorrect community$")
    public void userInvokesGetStreamzineCommandWithIncorrectCommunity() {
        for (UserDeviceData data : currentUserDevices) {
            PhoneState state = deviceSet.getPhoneState(data);
            UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

            ResponseEntity<StandardResponse> response = deviceSet
                .getStreamzine("some_unknown_community", data, token.getTimestampToken(), token.getTimestamp(), validResolution, state.getLastFacebookInfo().getUserName(), StandardResponse.class,
                               apiVersions);

            errorResponses.put(data, response);
        }
    }

    @Then("^user gets (.+) code in response$")
    public void userGetsHttpErrorCodeInResponse(final int code) {
        for (UserDeviceData data : currentUserDevices) {
            ResponseEntity<StandardResponse> response = errorResponses.get(data);
            assertEquals(getErrorMessage(data) + ", body: " + response.getBody(), code, response.getStatusCode().value());
        }
    }
}
