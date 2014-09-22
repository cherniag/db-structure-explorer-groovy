package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.applicationtests.services.http.facebook.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class GooglePlusHttpService extends AbstractHttpService {
    public GooglePlusUserDetailsDto login(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String googlePlusUserId) {
        ResponseEntity<String> stringResponseEntity = doLogin(deviceData, deviceUID, accountCheck, format, accessToken, googlePlusUserId, accountCheck.userName);

        GooglePlusUserDetailsDto googlePlusUserDetailsDto = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), JsonHelper.USER_DETAILS_PATH, GooglePlusUserDetailsDto.class);

        return googlePlusUserDetailsDto;
    }

    private ResponseEntity<String> doLogin(UserDeviceData deviceData, String deviceUID, AccountCheckDTO accountCheck, RequestFormat format, String accessToken, String googlePlusUserId, String userName) {
        UserDataCreator.TimestampTokenData userToken = userDataCreator.createUserToken(accountCheck.userToken);

        String uri = getUri(deviceData, "SIGN_IN_GOOGLE_PLUS", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_TOKEN", userToken.getTimestampToken());
        request.add("TIMESTAMP", userToken.getTimestamp());
        request.add("ACCESS_TOKEN", accessToken);
        request.add("DEVICE_TYPE", deviceData.getDeviceType());
        request.add("GOOGLE_PLUS_USER_ID", googlePlusUserId);
        request.add("USER_NAME", userName);
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        return restTemplate.postForEntity(uri, request, String.class);
    }
}
