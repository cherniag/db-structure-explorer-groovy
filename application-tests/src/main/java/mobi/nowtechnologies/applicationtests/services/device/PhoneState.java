package mobi.nowtechnologies.applicationtests.services.device;

import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneActivationDto;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.web.client.HttpClientErrorException;

public interface PhoneState {
    String getDeviceUID();

    String getEmail();

    AccountCheckDTO getLastAccountCheckResponse();

    HttpClientErrorException getLastFacebookErrorResponse();

    FacebookUserDetailsDto getLastFacebookInfo();

    PhoneActivationDto getPhoneActivationResponse();

    AccountCheckDTO getActivationResponse();

    String getLastSentXTofyToken();

    String getFacebookUserId();

    long getLastActivationEmailToken();

}
