package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;

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
public class GooglePlusHttpService extends AbstractHttpService {

    public GooglePlusResponse login(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String googlePlusUserId, String userName, String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, googlePlusUserId, userName, userToken).getBody();
    }

    public ResponseEntity<GooglePlusResponse> loginWithoutWithExpectedError(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String googlePlusUserId,
                                                                            String userName, String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, googlePlusUserId, userName, userToken);
    }

    public ResponseEntity<GooglePlusResponse> loginWithoutAccessToken(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String googlePlusUserId, String userName,
                                                                      String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, googlePlusUserId, userName, userToken, true);
    }

    private ResponseEntity<GooglePlusResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String googlePlusUserId, String userName,
                                                       String userToken) {
        return doLogin(deviceData, deviceUID, format, accessToken, googlePlusUserId, userName, userToken, false);
    }

    private ResponseEntity<GooglePlusResponse> doLogin(UserDeviceData deviceData, String deviceUID, RequestFormat format, String accessToken, String googlePlusUserId, String userName,
                                                       String userToken, boolean omitAccessToken) {
        UserDataCreator.TimestampTokenData googlePlusUserToken = userDataCreator.createUserToken(userToken);

        String uri = getUri(deviceData, "SIGN_IN_GOOGLE_PLUS", format);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

        builder.queryParam("USER_TOKEN", googlePlusUserToken.getTimestampToken());
        if (!omitAccessToken) {
            builder.queryParam("ACCESS_TOKEN", accessToken);
        }
        builder.queryParam("GOOGLE_PLUS_USER_ID", googlePlusUserId);
        builder.queryParam("USER_NAME", userName);
        builder.queryParam("TIMESTAMP", googlePlusUserToken.getTimestamp());
        builder.queryParam("DEVICE_TYPE", deviceData.getDeviceType());
        builder.queryParam("DEVICE_UID", deviceUID);

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponents build = builder.build();
        URI requestUri = build.toUri();

        logger.info("\nSending for to [{}], parameters [{}], headers [{}]", uri, build.getQueryParams(), headers);
        ResponseEntity<GooglePlusResponse> response = restTemplate.exchange(requestUri, HttpMethod.POST, entity, GooglePlusResponse.class);
        logger.info("Response entity [{}]\n", response);

        return response;
    }
}
