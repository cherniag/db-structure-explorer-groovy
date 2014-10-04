package mobi.nowtechnologies.applicationtests.services.device;

import mobi.nowtechnologies.applicationtests.services.http.domain.common.*;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.Error;
import mobi.nowtechnologies.applicationtests.services.http.facebook.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.HttpStatus;

public interface PhoneState {
    String getDeviceUID();

    String getEmail();

    AccountCheckDTO getLastAccountCheckResponse();

    mobi.nowtechnologies.applicationtests.services.http.domain.common.Error getLastFacebookErrorResponse();

    User getLastFacebookInfo();

    User getLastGooglePlusInfo();

    PhoneActivationDto getPhoneActivationResponse();

    AccountCheckDTO getActivationResponse();

    String getLastSentXTofyToken();

    String getFacebookUserId();

    String getGooglePlusUserId();

    Error getLastGooglePlusError();

    String getGooglePlusToken();

    long getLastActivationEmailToken();

    void setLastEnteredPhoneNumberOnWebPortal(String anyValid);

    String getLastEnteredPhoneNumberOnWebPortal();

    String getFacebookAccessToken();

    HttpStatus getLastFacebookErrorStatus();

    HttpStatus getLastGooglePlusErrorStatus();
}
