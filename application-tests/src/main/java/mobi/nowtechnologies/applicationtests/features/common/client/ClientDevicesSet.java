package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers.ServiceConfigHttpService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.ResponseWrapper;
import mobi.nowtechnologies.applicationtests.services.http.accountcheck.AccountCheckHttpService;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartHttpService;
import mobi.nowtechnologies.applicationtests.services.http.chart.ChartResponse;
import mobi.nowtechnologies.applicationtests.services.http.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.User;
import mobi.nowtechnologies.applicationtests.services.http.news.NewsHttpService;
import mobi.nowtechnologies.applicationtests.services.http.news.json.JsonNewsResponse;
import mobi.nowtechnologies.applicationtests.services.http.news.xml.XmlNewsResponse;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
import mobi.nowtechnologies.applicationtests.services.http.signup.SignupHttpService;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;

import javax.annotation.Resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

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
    @Resource
    protected NewsHttpService newsHttpService;
    @Resource
    protected ServiceConfigHttpService serviceConfigHttpService;

    protected Map<UserDeviceData, PhoneStateImpl> states = new ConcurrentHashMap<UserDeviceData, PhoneStateImpl>();

    //
    // Flow operations
    //
    public ResponseWrapper<AccountCheckDto> accountCheck(UserDeviceData userDeviceData) {
        final PhoneStateImpl state = states.get(userDeviceData);
        ResponseWrapper<AccountCheckDto> responseWrapper = accountCheckHttpService.accountCheck(userDeviceData,
                                                                                                state.getLastAccountCheckResponse().userName,
                                                                                                state.getLastAccountCheckResponse().userToken,
                                                                                                userDeviceData.getFormat());
        state.accountCheck = responseWrapper.getEntity();
        return responseWrapper;
    }

    public void accountCheckWithUrbanAirshipToken(UserDeviceData userDeviceData, String urbanAirshipToken) {
        final PhoneStateImpl state = states.get(userDeviceData);

        state.accountCheck = accountCheckHttpService
            .accountCheckWithUrbanAirshipToken(userDeviceData, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat(),
                                               urbanAirshipToken).getEntity();
    }

    public ResponseWrapper<AccountCheckDto> accountCheckFromIOS(UserDeviceData userDeviceData, String iTunesReceipt) {
        final PhoneStateImpl state = states.get(userDeviceData);
        ResponseWrapper<AccountCheckDto> responseWrapper = accountCheckHttpService.accountCheckFromIOS(userDeviceData,
                                                                                                       state.getLastAccountCheckResponse().userName,
                                                                                                       state.getLastAccountCheckResponse().userToken,
                                                                                                       userDeviceData.getFormat(),
                                                                                                       iTunesReceipt);
        state.accountCheck = responseWrapper.getEntity();
        return responseWrapper;
    }


    public ResponseEntity<String> serviceConfig(UserDeviceData userDeviceData, String header, ApiVersions apiVersions) {
        if (apiVersions.above("6.8").contains(userDeviceData.getApiVersion())) {
            return serviceConfigHttpService.serviceConfigXUserAgent(userDeviceData, header);
        } else {
            return serviceConfigHttpService.serviceConfigUserAgent(userDeviceData, header);
        }
    }

    //
    // Sign up
    //
    public void singup(UserDeviceData deviceData) {
        singup(deviceData, null, null, false, userDataCreator.generateDeviceUID());
    }

    public void singupWithUrbanAirshipToken(UserDeviceData deviceData, String urbanAirshipToken) {
        singup(deviceData, null, null, false, userDataCreator.generateDeviceUID());
    }

    public void singupWithOtherDevice(UserDeviceData userData, UserDeviceData otherDeviceData) {
        singup(userData, null, null, true, states.get(otherDeviceData).deviceUID);
    }

    public void singupWithNewDevice(UserDeviceData deviceData) {
        singup(deviceData, null, null, true, userDataCreator.generateDeviceUID());
    }

    public void singup(UserDeviceData deviceData, String xtifyToken) {
        singup(deviceData, xtifyToken, xtifyToken, true, userDataCreator.generateDeviceUID());
    }

    public void singupWithAppsFlyer(UserDeviceData deviceData, String appsFlyerUid) {
        singup(deviceData, null, appsFlyerUid, true, userDataCreator.generateDeviceUID());
    }

    public void singup(UserDeviceData deviceData, String xtifyToken, String appsFlyerUid, boolean overrideDeviceUID, String deviceUID) {
        PhoneStateImpl state = states.get(deviceData);

        // signup device could be called twice for the same device when user changes the phone (different device uid or changes the api version)
        if (state == null) {
            state = new PhoneStateImpl();
            state.email = userDataCreator.generateEmail();
            state.deviceUID = deviceUID;
            states.put(deviceData, state);
        } else if (overrideDeviceUID) {
            state.deviceUID = deviceUID;
        }

        state.lastSentXTofyToken = xtifyToken;
        state.accountCheck = signupHttpService.signUp(deviceData, state.getDeviceUID(), deviceData.getFormat(), xtifyToken, appsFlyerUid);

    }

    //
    // Get Chart
    //
    public ResponseEntity<ChartResponse> getChart(UserDeviceData deviceData) {
        return getChart(deviceData, HttpMethod.POST, null);
    }

    public ResponseEntity<ChartResponse> getChart(UserDeviceData deviceData, HttpMethod httpMethod) {
        return getChart(deviceData, httpMethod, null);
    }

    public ResponseEntity<ChartResponse> getChart(UserDeviceData deviceData, HttpMethod httpMethod, String resolution) {
        final PhoneState state = states.get(deviceData);

        return chartHttpService.getChart(deviceData, state, resolution, httpMethod);
    }

    public NewsDetailDto[] getNews(UserDeviceData deviceData, ApiVersions allVersions) {
        final PhoneState state = states.get(deviceData);

        boolean needToSendGet = allVersions.above("6.3").contains(deviceData.getApiVersion());

        if (deviceData.getFormat().json()) {
            if (needToSendGet) {
                ResponseEntity<JsonNewsResponse> entity = newsHttpService.getNews(deviceData, state, JsonNewsResponse.class);
                return entity.getBody().getResponse().get().getValue().getNewsDetailDtos();
            } else {
                ResponseEntity<JsonNewsResponse> entity = newsHttpService.postNews(deviceData, state, JsonNewsResponse.class);
                return entity.getBody().getResponse().get().getValue().getNewsDetailDtos();
            }
        } else {
            if (needToSendGet) {
                ResponseEntity<XmlNewsResponse> entity = newsHttpService.getNews(deviceData, state, XmlNewsResponse.class);
                return entity.getBody().getNews().getNewsDetailDtos();
            } else {
                ResponseEntity<XmlNewsResponse> entity = newsHttpService.postNews(deviceData, state, XmlNewsResponse.class);
                return entity.getBody().getNews().getNewsDetailDtos();
            }
        }
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

        public PhoneActivationDto phoneActivationDto;
        public AccountCheckDto activationResponse;
        public String lastSentXTofyToken;
        public String facebookUserId;
        public String googlePlusUserId;
        public long lastActivationEmailToken;
        public String facebookAccessToken;
        public HttpStatus lastFacebookErrorStatus;
        public String googlePlusToken;
        public Error lastGooglePlusError;
        public HttpStatus lastGooglePlusErrorStatus;
        public String urbanAirshipToken;
        String deviceUID;
        String email;
        AccountCheckDto accountCheck;
        Error lastFacebookError;
        User lastFacebookInfo;
        User lastGooglePlusUserInfo;
        private String lastEnteredPhoneNumberOnWebPortal;

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
        public AccountCheckDto getLastAccountCheckResponse() {
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
        public AccountCheckDto getActivationResponse() {
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
        public String getLastEnteredPhoneNumberOnWebPortal() {
            return lastEnteredPhoneNumberOnWebPortal;
        }

        @Override
        public void setLastEnteredPhoneNumberOnWebPortal(String lastEnteredPhoneNumberOnWebPortal) {
            this.lastEnteredPhoneNumberOnWebPortal = lastEnteredPhoneNumberOnWebPortal;
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
        public String getLastSocialActivationUserName() {
            User lastFacebookInfo = getLastFacebookInfo();
            if (lastFacebookInfo != null) {
                return lastFacebookInfo.getUserName();
            }

            User lastGooglePlusInfo = getLastGooglePlusInfo();

            Assert.notNull(lastGooglePlusInfo, "User did not activated with Facebook or Google Plus");

            return lastGooglePlusInfo.getUserName();
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
