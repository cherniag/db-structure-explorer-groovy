package mobi.nowtechnologies.applicationtests.services.http.phonenumber;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/2/2014
 */
@Service
public class PhoneNumberHttpService extends AbstractHttpService {

    public PhoneActivationDto phoneNumber(UserDeviceData deviceData, String phoneNumber, String userName, String userToken, RequestFormat format){
        final UserDataCreator.TimestampTokenData token = createUserToken(userToken);

        String uri = getUri(deviceData, "PHONE_NUMBER", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("PHONE", phoneNumber);
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());

        logger.info("Posting to [" + uri + "] request: [" + request + "] for device data: [" + deviceData + "]");

        String body = restTemplate.postForEntity(uri, request, String.class).getBody();

        logger.info("Response is [{}]", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.PHONE_NUMBER_PATH, PhoneActivationDto.class);
    }

}
