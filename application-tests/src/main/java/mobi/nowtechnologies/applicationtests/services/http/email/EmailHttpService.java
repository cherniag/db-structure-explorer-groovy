package mobi.nowtechnologies.applicationtests.services.http.email;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class EmailHttpService extends AbstractHttpService {
    public long generateEmail(RequestFormat format, UserDeviceData deviceData, String email, String deviceUID, String userName) {
        String uri = getUri(deviceData, "EMAIL_GENERATE", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("EMAIL", email);
        request.add("USER_NAME", userName);
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);

        // avoiding bad format: {Long:123} ... in response
        Map<String, Object> values = jsonHelper.extractObjectMapByPath(response.getBody(), JsonHelper.EMAIL_ACTIVATION_PATH);

        return Long.parseLong(values.get("Long").toString());
    }

    public void signIn(String email, String userToken, UserDeviceData deviceData, String deviceUID, RequestFormat format, String signInEmailId, String signInEmailToken) {
        String uri = getUri(deviceData, "SIGN_IN_EMAIL", format);

        UserDataCreator.TimestampTokenData token = createUserToken(userToken);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("EMAIL", email);
        request.add("EMAIL_ID", signInEmailId);
        request.add("TOKEN", signInEmailToken);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());
        request.add("DEVICE_UID", deviceUID);

        restTemplate.postForEntity(uri, request, String.class);
    }
}
