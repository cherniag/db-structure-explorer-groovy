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

import javax.annotation.Resource;

@Service
public class FacebookHttpService extends AbstractHttpService {
    @Resource
    private UserDataCreator userDataCreator;

    public FacebookUserDetailsDto login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, String timestamp, RequestFormat format, String accessToken, String facebookUserId) {
        ResponseEntity<String> stringResponseEntity = doLogin(deviceData, deviceUID, accountCheck, timestamp, format, accessToken, facebookUserId, accountCheck.userName);

        FacebookUserDetailsDto facebookUserDetailsDto = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), JsonHelper.USER_DETAILS_PATH, FacebookUserDetailsDto.class);

        return facebookUserDetailsDto;
    }

    public FacebookUserDetailsDto login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, String timestamp, RequestFormat format, String accessToken, String facebookUserId, String emailAsUserName) {
        ResponseEntity<String> stringResponseEntity = doLogin(deviceData, deviceUID, accountCheck, timestamp, format, accessToken, facebookUserId, emailAsUserName);

        FacebookUserDetailsDto facebookUserDetailsDto = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), JsonHelper.USER_DETAILS_PATH, FacebookUserDetailsDto.class);

        return facebookUserDetailsDto;
    }

    public HttpClientErrorException loginWithExpectedError(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, String timestamp, RequestFormat format, String accessToken, String facebookUserId) {
        try {
            doLogin(deviceData, deviceUID, accountCheck, timestamp, format, accessToken, facebookUserId, accountCheck.userName);
            return null;
        } catch (HttpClientErrorException e) {
            return e;
        }
    }

    private ResponseEntity<String> doLogin(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, String timestamp, RequestFormat format, String accessToken, String facebookUserId, String userName) {

        String uri = getUri(deviceData.getCommunityUrl(), deviceData.getApiVersion().getApiVersion(), "SIGN_IN_FACEBOOK", format);
        //
        // Build parameters
        //
        final String userToken = userDataCreator.createUserToken(accountCheck, timestamp);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("ACCESS_TOKEN", accessToken);
        request.add("USER_TOKEN", userToken);
        request.add("TIMESTAMP", timestamp);
        request.add("DEVICE_TYPE", deviceData.getDeviceType());
        request.add("FACEBOOK_USER_ID", facebookUserId);
        request.add("USER_NAME", userName);
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        return restTemplate.postForEntity(uri, request, String.class);
    }

}
