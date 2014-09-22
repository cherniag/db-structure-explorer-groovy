package mobi.nowtechnologies.applicationtests.services.device;

import mobi.nowtechnologies.applicationtests.services.http.domain.facebook.UserDetails;
import mobi.nowtechnologies.applicationtests.services.http.facebook.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public interface PhoneState {
    String getDeviceUID();

    String getEmail();

    AccountCheckDTO getLastAccountCheckResponse();

    mobi.nowtechnologies.applicationtests.services.http.domain.facebook.Error getLastFacebookErrorResponse();

    UserDetails getLastFacebookInfo();

    GooglePlusUserDetailsDto getLastGooglePlusInfo();

    PhoneActivationDto getPhoneActivationResponse();

    AccountCheckDTO getActivationResponse();

    String getLastSentXTofyToken();

    String getFacebookUserId();

    String getGooglePlusUserId();

    String getGooglePlusToken();

    long getLastActivationEmailToken();

    void setLastEnteredPhoneNumberOnWebPortal(String anyValid);

    String getLastEnteredPhoneNumberOnWebPortal();

    String getFacebookAccessToken();

    HttpStatus getLastFacebookErrorStatus();
}
