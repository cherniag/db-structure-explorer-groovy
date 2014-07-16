package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.features.social.facebook.PhoneState;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.signup.SignupHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ClientDevicesSet {
    // helpers
    @Resource
    protected UserDataCreator userDataCreator;
    // http
    @Resource
    protected SignupHttpService signupHttpService;
    @Resource
    protected ChartHttpService chartHttpService;

    protected RequestFormat format = RequestFormat.JSON;
    protected Map<UserDeviceData, PhoneStateImpl> states = new ConcurrentHashMap<UserDeviceData, PhoneStateImpl>();

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

    public String getChart(UserDeviceData deviceData, String userName, String timestamp, String userToken) {
        final PhoneState state = states.get(deviceData);

        return chartHttpService.getChart(deviceData.getCommunityUrl(), deviceData.getApiVersion().getApiVersion(), userName, userToken, timestamp, state.getDeviceUID(), format);
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

    static class PhoneStateImpl implements PhoneState {
        String deviceUID;
        String email;
        AccountCheckDTO accountCheck;
        HttpClientErrorException lastFacebookError;
        FacebookUserDetailsDto lastFacebookInfo;

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
