package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.common.standard.StandardResponse;
import mobi.nowtechnologies.applicationtests.services.http.context.ContextHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookResponse;
import mobi.nowtechnologies.applicationtests.services.http.googleplus.GooglePlusResponse;
import mobi.nowtechnologies.applicationtests.services.http.email.EmailHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator;
import mobi.nowtechnologies.applicationtests.services.http.googleplus.GooglePlusHttpService;
import mobi.nowtechnologies.applicationtests.services.http.googleplus.GooglePlusUserInfoGenerator;
import mobi.nowtechnologies.applicationtests.services.http.referral.ReferralHttpService;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.GetStreamzineHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.transport.referrals.ReferralDto;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MQAppClientDeviceSet extends ClientDevicesSet {

    @Resource
    FacebookUserInfoGenerator facebookUserInfoGenerator;
    @Resource
    GooglePlusUserInfoGenerator googlePlusUserInfoGenerator;
    @Resource
    FacebookHttpService facebookHttpService;
    @Resource
    GooglePlusHttpService googlePlusHttpService;
    @Resource
    EmailHttpService emailHttpService;
    @Resource
    GetStreamzineHttpService getStreamzineHttpService;
    @Resource
    ReferralHttpService referralHttpService;
    @Resource
    ContextHttpService contextHttpService;
    //
    // Flow operations
    //

    //
    // Facebook login types
    //
    public void loginUsingFacebook(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), accessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastFacebookInfo.getUserName(), state.lastFacebookInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingFacebookWithProfile(UserDeviceData deviceData, String facebookAccessToken, String facebookUserId) {
        final PhoneStateImpl state = states.get(deviceData);
        state.facebookAccessToken = facebookAccessToken;
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), facebookAccessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastFacebookInfo.getUserName(), state.lastFacebookInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingFacebookWithCityOnly(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(state.getEmail(), state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), accessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastFacebookInfo.getUserName(), state.getLastAccountCheckResponse().userToken, deviceData.getFormat());
    }

    public void loginUsingFacebookWithDefinedEmail(UserDeviceData deviceData, String email) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(email, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        state.email = email;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), accessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastFacebookInfo.getUserName(), state.getLastAccountCheckResponse().userToken, deviceData.getFormat());
    }

    public void loginUsingFacebookWithDefinedAccountIdAndEmail(UserDeviceData deviceData, String email, String facebookUserId) {
        final PhoneStateImpl state = states.get(deviceData);

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(email, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookUserId = facebookUserId;
        state.email = email;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), accessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastFacebookInfo.getUserName(), state.lastFacebookInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingFacebookWithOtherDevice(UserDeviceData userData, UserDeviceData otherDeviceData) {
        final PhoneStateImpl state = states.get(userData);
        final PhoneStateImpl otherState = states.get(otherDeviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(userData, otherState.getDeviceUID(), state.getLastAccountCheckResponse(), userData.getFormat(), accessToken, facebookUserId);
        state.accountCheck = accountCheckHttpService.accountCheck(userData, state.lastFacebookInfo.getUserName(), state.lastFacebookInfo.getUserToken(), userData.getFormat());
    }

    public void loginUsingGooglePlus(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken)
            .getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingGooglePlusWithProfile(UserDeviceData deviceData, String accessToken, String googlePlusUserId) {
        final PhoneStateImpl state = states.get(deviceData);

        state.googlePlusToken = accessToken;
        state.googlePlusUserId = googlePlusUserId;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken)
            .getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingGooglePlusWithOtherDevice(UserDeviceData userData, UserDeviceData otherDeviceData) {
        final PhoneStateImpl state = states.get(userData);
        final PhoneStateImpl otherState = states.get(otherDeviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(userData, otherState.getDeviceUID(), userData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName,
                   state.getLastAccountCheckResponse().userToken).getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(userData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), userData.getFormat());
    }

    public void loginUsingGooglePlusWithUpdatedDetails(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken)
            .getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingGooglePlusWithExactEmailAndGooglePlusId(UserDeviceData deviceData, String email, String googlePlusUserId) {
        final PhoneStateImpl state = states.get(deviceData);
        state.googlePlusUserId = googlePlusUserId;
        state.email = email;

        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusToken = accessToken;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken)
            .getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), deviceData.getFormat());
    }

    public void loginUsingGooglePlusWithExactEmail(UserDeviceData deviceData, String email) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(email, state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        state.email = email;
        state.lastGooglePlusUserInfo = googlePlusHttpService
            .login(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, state.googlePlusUserId, state.getLastAccountCheckResponse().userName,
                   state.getLastAccountCheckResponse().userToken).getUser();
        state.accountCheck = accountCheckHttpService.accountCheck(deviceData, state.lastGooglePlusUserInfo.getUserName(), state.lastGooglePlusUserInfo.getUserToken(), deviceData.getFormat());
    }

    //
    // Error flows
    //
    public void loginUsingFacebookWithEmptyEmail(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessToken(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        ResponseEntity<FacebookResponse> responseEntity = facebookHttpService
            .loginWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, facebookUserId, state.getLastAccountCheckResponse().userName,
                                    state.getLastAccountCheckResponse().userToken);

        state.lastFacebookError = responseEntity.getBody().getErrorMessage();
        state.lastFacebookErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingFacebookWithDifferentId(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithIdError(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        ResponseEntity<FacebookResponse> responseEntity = facebookHttpService
            .loginWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, facebookUserId, state.getLastAccountCheckResponse().userName,
                                    state.getLastAccountCheckResponse().userToken);

        state.lastFacebookError = responseEntity.getBody().getErrorMessage();
        state.lastFacebookErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingFacebookWithInvalidAccessToken(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithAccesstokenError(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        ResponseEntity<FacebookResponse> responseEntity = facebookHttpService
            .loginWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, facebookUserId, state.getLastAccountCheckResponse().userName,
                                    state.getLastAccountCheckResponse().userToken);

        state.lastFacebookError = responseEntity.getBody().getErrorMessage();
        state.lastFacebookErrorStatus = responseEntity.getStatusCode();
    }


    public void loginUsingFacebookWithInvalidFacebookId(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String email = "test@gmail.com";

        String accessToken = facebookUserInfoGenerator.createAccessToken(email, state.getLastAccountCheckResponse().userName, facebookUserId);
        ResponseEntity<FacebookResponse> responseEntity = facebookHttpService
            .loginWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, facebookUserId + 1, state.getLastAccountCheckResponse().userName,
                                    state.getLastAccountCheckResponse().userToken);

        state.lastFacebookError = responseEntity.getBody().getErrorMessage();
        state.lastFacebookErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingFacebookWithoutAccessToken(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        state.lastFacebookErrorStatus =
            facebookHttpService.loginWithoutAccessToken(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), deviceData.getFormat(), accessToken, facebookUserId).getStatusCode();
    }

    public void loginUsingFacebookBadUserName(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        AccountCheckDTO lastAccountCheckResponse = state.getLastAccountCheckResponse();
        String accessToken = facebookUserInfoGenerator.createAccessToken(state.getEmail(), lastAccountCheckResponse.userName, facebookUserId);
        state.facebookAccessToken = accessToken;
        state.facebookUserId = facebookUserId;
        ResponseEntity<FacebookResponse> responseEntity =
            facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, facebookUserId, "badName", lastAccountCheckResponse.userToken);
        state.lastFacebookErrorStatus = responseEntity.getStatusCode();
        state.lastFacebookError = responseEntity.getBody().getErrorMessage();
    }

    public void loginUsingGooglePlusWithoutAccessToken(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        ResponseEntity<GooglePlusResponse> responseEntity = googlePlusHttpService
            .loginWithoutAccessToken(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName,
                                     state.getLastAccountCheckResponse().userToken);
        state.lastGooglePlusError = responseEntity.getBody().getErrorMessage();
        state.lastGooglePlusErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingGooglePlusWithInvalidGooglePlusId(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        ResponseEntity<GooglePlusResponse> responseEntity = googlePlusHttpService
            .loginWithoutWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId + 1, state.getLastAccountCheckResponse().userName,
                                           state.getLastAccountCheckResponse().userToken);
        state.lastGooglePlusError = responseEntity.getBody().getErrorMessage();
        state.lastGooglePlusErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingGooglePlusWithEmptyEmail(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken("", state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        ResponseEntity<GooglePlusResponse> responseEntity = googlePlusHttpService
            .loginWithoutWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName,
                                           state.getLastAccountCheckResponse().userToken);
        state.lastGooglePlusError = responseEntity.getBody().getErrorMessage();
        state.lastGooglePlusErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingGooglePlusWithInvalidAuthToken(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessTokenWithAuthError(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;
        ResponseEntity<GooglePlusResponse> responseEntity = googlePlusHttpService
            .loginWithoutWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, state.getLastAccountCheckResponse().userName,
                                           state.getLastAccountCheckResponse().userToken);
        state.lastGooglePlusError = responseEntity.getBody().getErrorMessage();
        state.lastGooglePlusErrorStatus = responseEntity.getStatusCode();
    }

    public void loginUsingGooglePlusBadAuth(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String googlePlusUserId = System.nanoTime() + "";
        String accessToken = googlePlusUserInfoGenerator.createAccessToken(state.getEmail(), state.getLastAccountCheckResponse().userName, googlePlusUserId);
        state.googlePlusUserId = googlePlusUserId;
        state.googlePlusToken = accessToken;

        ResponseEntity<GooglePlusResponse> responseEntity = googlePlusHttpService
            .loginWithoutWithExpectedError(deviceData, state.getDeviceUID(), deviceData.getFormat(), accessToken, googlePlusUserId, "badUsername", state.getLastAccountCheckResponse().userToken);

        state.lastGooglePlusError = responseEntity.getBody().getErrorMessage();
        state.lastGooglePlusErrorStatus = responseEntity.getStatusCode();
    }

    //
    // Email flow operations
    // register (generate email)
    //
    public void registerEmail(UserDeviceData deviceData) {
        PhoneStateImpl state = states.get(deviceData);
        String email = state.getEmail();
        state.lastActivationEmailToken = emailHttpService.generateEmail(deviceData.getFormat(), deviceData, email, state.getDeviceUID(), state.getDeviceUID());
    }

    public void registerEmail(UserDeviceData deviceData, String email) {
        PhoneStateImpl state = states.get(deviceData);
        state.email = email;
        state.lastActivationEmailToken = emailHttpService.generateEmail(deviceData.getFormat(), deviceData, email, state.getDeviceUID(), state.getDeviceUID());
    }

    //
    // Sign in (hit the link in email)
    //
    public void signInEmail(UserDeviceData deviceData, String signInEmailId, String signInEmailToken) {
        PhoneState state = states.get(deviceData);

        String email = state.getEmail();
        String userToken = state.getLastAccountCheckResponse().userToken;

        emailHttpService.signIn(email, userToken, deviceData, state.getDeviceUID(), deviceData.getFormat(), signInEmailId, signInEmailToken);
    }

    public void signInEmail(UserDeviceData deviceData, String email, String signInEmailId, String signInEmailToken) {
        PhoneState state = states.get(deviceData);

        String userToken = state.getLastAccountCheckResponse().userToken;

        emailHttpService.signIn(email, userToken, deviceData, state.getDeviceUID(), deviceData.getFormat(), signInEmailId, signInEmailToken);
    }

    //
    // Streamzine
    //
    public <T> ResponseEntity<T> getStreamzine(String community, UserDeviceData deviceData, String timestampToken, String timestamp, String resolution, String userName, Class<T> type,
                                               ApiVersions apiVersions) {
        PhoneStateImpl state = states.get(deviceData);

        // get or post?
        if (apiVersions.bellow("6.3").contains(deviceData.getApiVersion())) {
            return getStreamzineHttpService.postStreamzine(community, deviceData, state, timestampToken, timestamp, resolution, userName, type);
        } else {
            return getStreamzineHttpService.getStreamzine(community, deviceData, state, timestampToken, timestamp, resolution, userName, type);
        }
    }

    public ResponseEntity<StandardResponse> getStreamzineErrorEntity(UserDeviceData deviceData, String timestampToken, String timestamp, String resolution, String userName, ApiVersions apiVersions) {
        PhoneStateImpl state = states.get(deviceData);

        // get or post?
        if (apiVersions.bellow("6.3").contains(deviceData.getApiVersion())) {
            return getStreamzineHttpService.postStreamzine(deviceData.getCommunityUrl(), deviceData, state, timestampToken, timestamp, resolution, userName, StandardResponse.class);
        } else {
            return getStreamzineHttpService.getStreamzine(deviceData.getCommunityUrl(), deviceData, state, timestampToken, timestamp, resolution, userName, StandardResponse.class);
        }
    }

    //
    // Referrals
    //
    public ResponseEntity<String> postReferrals(UserDeviceData data, List<ReferralDto> referrals) {
        PhoneState state = states.get(data);
        return referralHttpService.postReferrals(data, state, referrals, String.class);
    }

    public ResponseEntity<String> context(UserDeviceData data) {
        PhoneState state = states.get(data);
        return contextHttpService.context(data, state);
    }

}
