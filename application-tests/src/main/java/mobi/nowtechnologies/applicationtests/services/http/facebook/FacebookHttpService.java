package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;

import java.net.URI;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FacebookHttpService extends AbstractHttpService {

    public mobi.nowtechnologies.applicationtests.services.http.domain.common.User login(UserDeviceData deviceData, String deviceUID, AccountCheckDto accountCheck, RequestFormat format,
                                                                                        String accessToken, String facebookUserId) {
        ResponseEntity<FacebookResponse> responseEntity = doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, accountCheck.userName, accountCheck.userToken);

        return responseEntity.getBody().getUser();
    }

    public ResponseEntity<FacebookResponse> loginWithExpectedError(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName,
                                                                   String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, userName, userToken);
    }

    public ResponseEntity<FacebookResponse> loginWithoutAccessToken(UserDeviceData deviceData, String deviceUID, AccountCheckDto accountCheck, RequestFormat format, String accessToken,
                                                                    String facebookUserId) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, accountCheck.userName, accountCheck.userToken, true);
    }

    private ResponseEntity<FacebookResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName, String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, facebookUserId, userName, userToken, false);
    }

    private ResponseEntity<FacebookResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String facebookUserId, String userName,
                                                     String accountUserToken, boolean omitAccessToken) {
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

        UriComponents build = builder.build();
        URI requestUri = build.toUri();

        logger.info("\nSending for [{}] to [{}] headers [{}], parameters [{}]", deviceData, uri, headers, build.getQueryParams());
        ResponseEntity<FacebookResponse> response = restTemplate.exchange(requestUri, HttpMethod.POST, entity, FacebookResponse.class);
        logger.info("Response entity [{}]\n", response);


        return response;
    }

}
