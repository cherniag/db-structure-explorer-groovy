package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
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
        singup(deviceData, null);
    }

    public void singup(UserDeviceData deviceData, String xtifyToken) {
        PhoneStateImpl state = states.get(deviceData);

        // signup device could be called twice for the same device when user changes the phone (different device uid or changes the api version)
        if(state == null) {
            state = new PhoneStateImpl();
            state.deviceUID = userDataCreator.generateDeviceUID();
            state.email = userDataCreator.generateEmail();
            states.put(deviceData, state);
        }

        state.lastSentXTofyToken = xtifyToken;
        state.accountCheck = signupHttpService.signup(deviceData, state.getDeviceUID(), format, xtifyToken);

    }

    //
    // Get Chart
    //
    public String getChart(UserDeviceData deviceData, String userName) {
        final PhoneState state = states.get(deviceData);

        return chartHttpService.getChart(deviceData, userName, state.getLastAccountCheckResponse().userToken, state.getDeviceUID(), format);
    }

    //
    // State operations
    //

    public void cleanup() {
        states.clear();
    }

    public void changePhone(UserDeviceData deviceData) {
        PhoneStateImpl state = states.get(deviceData);

        // the same email but device_uid is different
        PhoneStateImpl newPhone = new PhoneStateImpl();
        newPhone.deviceUID = userDataCreator.generateDeviceUID();
        newPhone.email = state.email;

        states.put(deviceData, newPhone);
    }

    public void changeEmail(UserDeviceData deviceData) {
        PhoneStateImpl state = states.get(deviceData);

        // the same email but device_uid is different
        PhoneStateImpl newPhone = new PhoneStateImpl(state);
        newPhone.email = userDataCreator.generateEmail();

        states.put(deviceData, newPhone);
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
        public PhoneActivationDto phoneActivationDto;
        public AccountCheckDTO activationResponse;
        public String lastSentXTofyToken;
        public String facebookUserId;
        public long lastActivationEmailToken;

        PhoneStateImpl() {
        }

        public PhoneStateImpl(PhoneStateImpl state) {
            deviceUID = state.deviceUID;
            email = state.email;
            accountCheck = state.accountCheck;
            lastFacebookError = state.lastFacebookError;
            lastFacebookInfo = state.lastFacebookInfo;
            phoneActivationDto = state.phoneActivationDto;
            activationResponse = state.activationResponse;
            lastSentXTofyToken = state.lastSentXTofyToken;
            facebookUserId = state.facebookUserId;
            lastActivationEmailToken = state.lastActivationEmailToken;
        }

        @Override
        public String getDeviceUID() {
            return deviceUID;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public AccountCheckDTO getLastAccountCheckResponse() {
            return accountCheck;
        }

        @Override
        public HttpClientErrorException getLastFacebookErrorResponse() {
            return lastFacebookError;
        }

        @Override
        public FacebookUserDetailsDto getLastFacebookInfo() {
            return lastFacebookInfo;
        }

        @Override
        public PhoneActivationDto getPhoneActivationResponse() {
            return phoneActivationDto;
        }

        @Override
        public AccountCheckDTO getActivationResponse() {
            return activationResponse;
        }

        @Override
        public String getLastSentXTofyToken() {
            return lastSentXTofyToken;
        }

        @Override
        public String getFacebookUserId() {
            return facebookUserId;
        }

        @Override
        public long getLastActivationEmailToken() {
            return lastActivationEmailToken;
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
