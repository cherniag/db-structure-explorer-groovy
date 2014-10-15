package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.accountcheck.AccountCheckHttpService;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartHttpService;
import mobi.nowtechnologies.applicationtests.services.http.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.User;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
import mobi.nowtechnologies.applicationtests.services.http.signup.SignupHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.HttpStatus;

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
    @Resource
    protected AccountCheckHttpService accountCheckHttpService;

    protected Map<UserDeviceData, PhoneStateImpl> states = new ConcurrentHashMap<UserDeviceData, PhoneStateImpl>();

    //
    // Flow operations
    //
    public void accountCheck(UserDeviceData userDeviceData){
        final PhoneStateImpl state = states.get(userDeviceData);
        state.accountCheck = accountCheckHttpService.accountCheck(userDeviceData, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat());
    }

    public void accountCheckFromIOS(UserDeviceData userDeviceData, String iTunesReceipt){
        final PhoneStateImpl state = states.get(userDeviceData);
        state.accountCheck = accountCheckHttpService.accountCheckFromIOS(userDeviceData, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat(), iTunesReceipt);
    }


    //
    // Sign up
    //
    public void singup(UserDeviceData deviceData) {
        singup(deviceData, null, false, userDataCreator.generateDeviceUID());
    }

    public void singupWithOtherDevice(UserDeviceData userData, UserDeviceData otherDeviceData) {
        singup(userData, null, true, states.get(otherDeviceData).deviceUID);
    }

    public void singupWithNewDevice(UserDeviceData deviceData) {
        singup(deviceData, null, true, userDataCreator.generateDeviceUID());
    }

    public void singup(UserDeviceData deviceData, String xtifyToken) {
        singup(deviceData, xtifyToken, true, userDataCreator.generateDeviceUID());
    }

    public void singup(UserDeviceData deviceData, String xtifyToken, boolean overrideDeviceUID, String deviceUID) {
        PhoneStateImpl state = states.get(deviceData);

        // signup device could be called twice for the same device when user changes the phone (different device uid or changes the api version)
        if(state == null) {
            state = new PhoneStateImpl();
            state.email = userDataCreator.generateEmail();
            state.deviceUID = deviceUID;
            states.put(deviceData, state);
        }
        else if (overrideDeviceUID) {
            state.deviceUID = deviceUID;
        }

        state.lastSentXTofyToken = xtifyToken;
        state.accountCheck = signupHttpService.signUp(deviceData, state.getDeviceUID(), deviceData.getFormat(), xtifyToken);

    }

    //
    // Get Chart
    //
    public String getChart(UserDeviceData deviceData, String userName) {
        final PhoneState state = states.get(deviceData);

        return chartHttpService.getChart(deviceData, userName, state.getLastAccountCheckResponse().userToken, state.getDeviceUID(), deviceData.getFormat());
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

    public PhoneState getPhoneState(UserDeviceData deviceData) {
        return states.get(deviceData);
    }



    static class PhoneStateImpl implements PhoneState {
        String deviceUID;
        String email;
        AccountCheckDTO accountCheck;
        Error lastFacebookError;
        User lastFacebookInfo;
        User lastGooglePlusUserInfo;
        public PhoneActivationDto phoneActivationDto;
        public AccountCheckDTO activationResponse;
        public String lastSentXTofyToken;
        public String facebookUserId;
        public String googlePlusUserId;
        public long lastActivationEmailToken;
        private String lastEnteredPhoneNumberOnWebPortal;
        public String facebookAccessToken;
        public HttpStatus lastFacebookErrorStatus;
        public String googlePlusToken;
        public Error lastGooglePlusError;
        public HttpStatus lastGooglePlusErrorStatus;

        PhoneStateImpl() {
        }

        public PhoneStateImpl(PhoneStateImpl state) {
            deviceUID = state.deviceUID;
            email = state.email;
            accountCheck = state.accountCheck;
            lastFacebookError = state.lastFacebookError;
            lastFacebookInfo = state.lastFacebookInfo;
            lastGooglePlusUserInfo = state.lastGooglePlusUserInfo;
            phoneActivationDto = state.phoneActivationDto;
            activationResponse = state.activationResponse;
            lastSentXTofyToken = state.lastSentXTofyToken;
            facebookUserId = state.facebookUserId;
            googlePlusUserId = state.googlePlusUserId;
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
        public Error getLastFacebookErrorResponse() {
            return lastFacebookError;
        }

        @Override
        public User getLastFacebookInfo() {
            return lastFacebookInfo;
        }

        @Override
        public User getLastGooglePlusInfo() {
            return lastGooglePlusUserInfo;
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
        public String getGooglePlusUserId() {
            return googlePlusUserId;
        }

        @Override
        public long getLastActivationEmailToken() {
            return lastActivationEmailToken;
        }

        @Override
        public void setLastEnteredPhoneNumberOnWebPortal(String lastEnteredPhoneNumberOnWebPortal) {
            this.lastEnteredPhoneNumberOnWebPortal = lastEnteredPhoneNumberOnWebPortal;
        }

        @Override
        public String getLastEnteredPhoneNumberOnWebPortal() {
            return lastEnteredPhoneNumberOnWebPortal;
        }

        @Override
        public String getFacebookAccessToken() {
            return facebookAccessToken;
        }

        @Override
        public HttpStatus getLastFacebookErrorStatus() {
            return lastFacebookErrorStatus;
        }

        @Override
        public HttpStatus getLastGooglePlusErrorStatus() {
            return lastGooglePlusErrorStatus;
        }

        @Override
        public Error getLastGooglePlusError() {
            return lastGooglePlusError;
        }

        @Override
        public String getGooglePlusToken() {
            return googlePlusToken;
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
