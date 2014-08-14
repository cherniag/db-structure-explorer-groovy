package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class FacebookHttpService extends AbstractHttpService {
    public FacebookUserDetailsDto login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId) {
        ResponseEntity<String> stringResponseEntity = doLogin(deviceData, deviceUID, accountCheck, format, accessToken, facebookUserId, accountCheck.userName);

        FacebookUserDetailsDto facebookUserDetailsDto = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), JsonHelper.USER_DETAILS_PATH, FacebookUserDetailsDto.class);

        return facebookUserDetailsDto;
    }

    public FacebookUserDetailsDto login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId, String emailAsUserName) {
        ResponseEntity<String> stringResponseEntity = doLogin(deviceData, deviceUID, accountCheck, format, accessToken, facebookUserId, emailAsUserName);

        FacebookUserDetailsDto facebookUserDetailsDto = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), JsonHelper.USER_DETAILS_PATH, FacebookUserDetailsDto.class);

        return facebookUserDetailsDto;
    }

    public HttpClientErrorException loginWithExpectedError(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId) {
        try {
            doLogin(deviceData, deviceUID, accountCheck, format, accessToken, facebookUserId, accountCheck.userName);
            return null;
        } catch (HttpClientErrorException e) {
            return e;
        }
    }

    private ResponseEntity<String> doLogin(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId, String userName) {
        UserDataCreator.TimestampTokenData userToken = userDataCreator.createUserToken(accountCheck.userToken);

        String uri = getUri(deviceData, "SIGN_IN_FACEBOOK", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("ACCESS_TOKEN", accessToken);
        request.add("USER_TOKEN", userToken.getTimestampToken());
        request.add("TIMESTAMP", userToken.getTimestamp());
        request.add("DEVICE_TYPE", deviceData.getDeviceType());
        request.add("FACEBOOK_USER_ID", facebookUserId);
        request.add("USER_NAME", userName);
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        return restTemplate.postForEntity(uri, request, String.class);
    }

}
