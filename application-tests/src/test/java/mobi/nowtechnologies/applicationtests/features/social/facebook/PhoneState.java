package mobi.nowtechnologies.applicationtests.features.social.facebook;

import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.web.client.HttpClientErrorException;

public interface PhoneState {
    String getDeviceUID();

    String getEmail();

    AccountCheckDTO getAccountCheck();

    HttpClientErrorException getLastFacebookError();

    FacebookUserDetailsDto getLastFacebookInfo();
}
