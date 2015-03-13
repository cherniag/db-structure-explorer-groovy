package mobi.nowtechnologies.applicationtests.services.http.signup;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class SignupHttpService extends AbstractHttpService {

    public AccountCheckDto signUp(UserDeviceData deviceData, String deviceUID, RequestFormat format, String xtifyToken, String appsFlyerUid) {
        String uri = getUri(deviceData, "SIGN_UP_DEVICE", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("DEVICE_TYPE", deviceData.getDeviceType());
        request.add("DEVICE_UID", deviceUID);
        if (xtifyToken != null) {
            // empty are allowed
            request.add("XTIFY_TOKEN", xtifyToken);
        }

        if (appsFlyerUid != null) {
            // empty are allowed
            request.add("APPSFLYER_UID", appsFlyerUid);
        }

        logger.info("\nSending for to [" + uri + "] request: [" + request + "] for device data: [" + deviceData + "]");
        String body = restTemplate.postForEntity(uri, request, String.class).getBody();
        logger.info("Response body [{}]\n", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDto.class);
    }
}
