package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.email.EmailHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MQAppClientDeviceSet extends ClientDevicesSet {
    @Resource
    private FacebookUserInfoGenerator facebookUserInfoGenerator;
    @Resource
    private FacebookHttpService facebookHttpService;
    @Resource
    private EmailHttpService emailHttpService;
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
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithCityOnly(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(state.getEmail(), state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookUserId = facebookUserId;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithDefinedAccountIdAndEmail(UserDeviceData deviceData, String email, String facebookUserId) {
        final PhoneStateImpl state = states.get(deviceData);

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(email, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.facebookUserId = facebookUserId;
        state.email = email;
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    //
    // Error flows
    //
    public void loginUsingFacebookWithEmptyEmail(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessToken(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithDifferentId(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithIdError(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithInvalidAccessToken(UserDeviceData deviceData) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithAccesstokenError(emptyEmail, state.getLastAccountCheckResponse().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getLastAccountCheckResponse(), format, accessToken, facebookUserId);
    }

    //
    // Email flow operations
    // register (generate email)
    //
    public void registerEmail(UserDeviceData deviceData) {
        PhoneStateImpl state = states.get(deviceData);
        String email = state.getEmail();
        state.lastActivationEmailToken = emailHttpService.generateEmail(format, deviceData, email, state.getDeviceUID(), state.getDeviceUID());
    }

    //
    // Sign in (hit the link in email)
    //
    public void signInEmail(UserDeviceData deviceData, String signInEmailId, String signInEmailToken) {
        PhoneState state = states.get(deviceData);

        String email = state.getEmail();
        String userToken = state.getLastAccountCheckResponse().userToken;

        emailHttpService.signIn(email, userToken, deviceData, state.getDeviceUID(), format, signInEmailId, signInEmailToken);
    }
}
