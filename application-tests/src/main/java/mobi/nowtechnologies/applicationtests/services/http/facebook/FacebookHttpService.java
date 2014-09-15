package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.applicationtests.services.http.domain.facebook.FacebookResponse;
import mobi.nowtechnologies.applicationtests.services.http.domain.facebook.UserDetails;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Service
public class FacebookHttpService extends AbstractHttpService {

    public UserDetails login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId) {
        ResponseEntity<FacebookResponse> responseEntity = doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, accountCheck.userName, accountCheck.userToken);

        return responseEntity.getBody().getUser().getUserDetails();
    }

    public UserDetails login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId, String emailAsUserName) {
        ResponseEntity<FacebookResponse> responseEntity = doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, emailAsUserName, accountCheck.userToken);

        return responseEntity.getBody().getUser().getUserDetails();
    }

    public ResponseEntity<FacebookResponse> loginWithExpectedError(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName, String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, userName, userToken);
    }

    public ResponseEntity<FacebookResponse> loginWithoutAccessToken(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String facebookUserId) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, accountCheck.userName, accountCheck.userToken, true);
    }

    private ResponseEntity<FacebookResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName, String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, userName, userToken, false);
    }

    private ResponseEntity<FacebookResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName, String accountUserToken, boolean omitAccessToken) {
        UserDataCreator.TimestampTokenData userToken = userDataCreator.createUserToken(accountUserToken);

        String uri = getUri(deviceData, "SIGN_IN_FACEBOOK", format);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        builder.queryParam("USER_TOKEN", userToken.getTimestampToken());
        builder.queryParam("TIMESTAMP", userToken.getTimestamp());
        builder.queryParam("DEVICE_TYPE", deviceData.getDeviceType());
        builder.queryParam("FACEBOOK_USER_ID", facebookUserId);
        builder.queryParam("USER_NAME", userName);
        builder.queryParam("DEVICE_UID", deviceUID);
        if (!omitAccessToken) {
            builder.queryParam("ACCESS_TOKEN", accessToken);
        }

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        URI requestUri = builder.build().toUri();
        logger.info("Posting to [" + requestUri + "]");

        return restTemplate.exchange(requestUri, HttpMethod.POST, entity, FacebookResponse.class);
    }

}
