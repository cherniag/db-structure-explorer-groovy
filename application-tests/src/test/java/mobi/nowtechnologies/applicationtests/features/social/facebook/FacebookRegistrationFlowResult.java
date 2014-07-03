package mobi.nowtechnologies.applicationtests.features.social.facebook;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator;
import mobi.nowtechnologies.applicationtests.services.http.signup.SignupHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FacebookRegistrationFlowResult {
    // helpers
    @Resource
    private UserDataCreator userDataCreator;
    @Resource
    private FacebookUserInfoGenerator facebookUserInfoGenerator;
    // http
    @Resource
    private SignupHttpService signupHttpService;
    @Resource
    private FacebookHttpService facebookHttpService;
    @Resource
    private ChartHttpService chartHttpService;

    private RequestFormat format = RequestFormat.JSON;
    private Map<UserDeviceData, PhoneStateImpl> states = new ConcurrentHashMap<UserDeviceData, PhoneStateImpl>();

    //
    // Flow operations
    //

    //
    // Sign up
    //
    public void singup(UserDeviceData deviceData) {
        PhoneStateImpl state = new PhoneStateImpl();
        state.deviceUID = userDataCreator.generateDeviceUID();
        state.email = userDataCreator.generateEmail();
        state.accountCheck = signupHttpService.signup(deviceData, state.getDeviceUID(), format);

        states.put(deviceData, state);
    }

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

    public String getChart(UserDeviceData deviceData, String userName, String timestamp, String userToken) {
        final PhoneState state = states.get(deviceData);

        return chartHttpService.getChart(deviceData.getCommunityUrl(), deviceData.getApiVersion().getApiVersion(), userName, userToken, timestamp, state.getDeviceUID());
    }

    //
    // Setters and getters
    //
    public void setFormat(RequestFormat format) {
        this.format = format;
    }

    public PhoneState getPhoneState(UserDeviceData deviceData) {
        return states.get(deviceData);
    }

    private static class PhoneStateImpl implements PhoneState {
        private String deviceUID;
        private String email;
        private AccountCheckDTO accountCheck;
        private HttpClientErrorException lastFacebookError;
        public FacebookUserDetailsDto lastFacebookInfo;

        @Override
        public String getDeviceUID() {
            return deviceUID;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public AccountCheckDTO getAccountCheck() {
            return accountCheck;
        }

        @Override
        public HttpClientErrorException getLastFacebookError() {
            return lastFacebookError;
        }

        @Override
        public FacebookUserDetailsDto getLastFacebookInfo() {
            return lastFacebookInfo;
        }

        @Override
        public String toString() {
            return "PhoneStateImpl{" +
                    "deviceUID='" + deviceUID + '\'' +
                    ", email='" + email + '\'' +
                    ", accountCheck=" + accountCheck +
                    ", lastFacebookError=" + lastFacebookError +
                    '}';
        }
    }

}
