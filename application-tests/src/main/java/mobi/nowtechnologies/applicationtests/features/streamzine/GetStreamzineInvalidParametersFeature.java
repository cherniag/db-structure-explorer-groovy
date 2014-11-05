package mobi.nowtechnologies.applicationtests.features.streamzine;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.ValidType;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.common.standard.StandardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Component
public class GetStreamzineInvalidParametersFeature extends AbstractStreamzineFeature {
    private Map<UserDeviceData, ResponseEntity<StandardResponse>> errorResponses = new HashMap<UserDeviceData, ResponseEntity<StandardResponse>>();
    private Map<UserDeviceData, String> spoiledOrNotUserNames = new HashMap<UserDeviceData, String>();

    //
    // Given and After
    //
    @Given("^First time user with device using (.+) format for (.+) and (.+) and for (.+) available$")
    public void firstTimeUserUsingFormat(@Transform(DictionaryTransformer.class) Word requestFormats,
                                        @Transform(DictionaryTransformer.class) Word versions,
                                        @Transform(DictionaryTransformer.class) Word communities,
                                        @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        // init once for examples table
        if(currentUserDevices.isEmpty()) {
            currentUserDevices = super.initUserData(requestFormats.set(RequestFormat.class), versions, communities, devices);
        }
    }

    @After
    public void cleanDevicesSet() {
        errorResponses.clear();
    }

    @When("^user invokes get streamzine for the (.+), (.+), (.+), (.+) parameters$")
    public void userSendsParameters(@Transform(NullableStringTransformer.class) NullableString nullable,
                                    ValidType timestamp,
                                    ValidType userName,
                                    ValidType userToken) {
        for (UserDeviceData data : currentUserDevices) {
            PhoneState state = deviceSet.getPhoneState(data);
            UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

            String userNameWrongOrCorrect = userName.decide(state.getLastFacebookInfo().getUserName());
            spoiledOrNotUserNames.put(data, userNameWrongOrCorrect);

            ResponseEntity<StandardResponse> response = deviceSet.getStreamzineErrorEntity(
                    data,
                    userToken.decide(token.getTimestampToken()),
                    timestamp.decide(token.getTimestamp()),
                    nullable.value(),
                    userNameWrongOrCorrect);

            errorResponses.put(data, response);
        }
    }

    @Then("^user gets (.+) code in response and (.+), (.+) also (.+) in the message body$")
    public void errorCodeAndMessages(final int httpCode,
                                     final int errorCode,
                                     @Transform(NullableStringTransformer.class) NullableString messageValue,
                                     @Transform(NullableStringTransformer.class) NullableString displayMessageValue) {
        for (UserDeviceData data : currentUserDevices) {
            ResponseEntity<StandardResponse> response = errorResponses.get(data);

            assertEquals(getErrorMessage(data),
                    Integer.valueOf(httpCode),
                    Integer.valueOf(response.getStatusCode().value())
            );

            if(errorCode > 0) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("username", spoiledOrNotUserNames.get(data));
                model.put("community", data.getCommunityUrl());

                final String message = interpolator.interpolate(messageValue.value(), model);
                final String displayMessage = interpolator.interpolate(displayMessageValue.value(), model);

                assertEquals(getErrorMessage(data),
                        Integer.valueOf(errorCode),
                        Integer.valueOf(response.getBody().getErrorMessage().getErrorCode())
                );
                assertEquals(getErrorMessage(data),
                        message,
                        response.getBody().getErrorMessage().getMessage()
                );
                assertEquals(getErrorMessage(data),
                        displayMessage,
                        response.getBody().getErrorMessage().getDisplayMessage()
                );
            }
        }

        spoiledOrNotUserNames.clear();
    }
}
