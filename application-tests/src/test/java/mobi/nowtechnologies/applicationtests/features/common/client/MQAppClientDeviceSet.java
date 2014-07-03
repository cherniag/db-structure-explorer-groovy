package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
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

    //
    // Flow operations
    //

    //
    // Facebook login types
    //
    public void loginUsingFacebook(UserDeviceData deviceData, String timestamp) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessToken(state.getEmail(), state.getAccountCheck().userName, facebookUserId);
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getAccountCheck(), timestamp, format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithCityOnly(UserDeviceData deviceData, String timestamp) {
        final PhoneStateImpl state = states.get(deviceData);

        String facebookUserId = System.nanoTime() + "";
        String accessToken = facebookUserInfoGenerator.createAccessTokenWithCityOnly(state.getEmail(), state.getAccountCheck().userName, facebookUserId);
        state.lastFacebookInfo = facebookHttpService.login(deviceData, state.getDeviceUID(), state.getAccountCheck(), timestamp, format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithEmptyEmail(UserDeviceData deviceData, String timestamp) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessToken(emptyEmail, state.getAccountCheck().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getAccountCheck(), timestamp, format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithDifferentId(UserDeviceData deviceData, String timestamp) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithIdError(emptyEmail, state.getAccountCheck().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getAccountCheck(), timestamp, format, accessToken, facebookUserId);
    }

    public void loginUsingFacebookWithInvalidAccessToken(UserDeviceData deviceData, String timestamp) {
        final PhoneStateImpl state = states.get(deviceData);

        final String facebookUserId = System.nanoTime() + "";
        final String emptyEmail = "";

        String accessToken = facebookUserInfoGenerator.createAccessTokenWithAccesstokenError(emptyEmail, state.getAccountCheck().userName, facebookUserId);
        state.lastFacebookError = facebookHttpService.loginWithExpectedError(deviceData, state.getDeviceUID(), state.getAccountCheck(), timestamp, format, accessToken, facebookUserId);
    }

}
