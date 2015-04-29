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
 * Author: Gennadii Cherniaiev Date: 7/2/2014
 */
@Service
public class PhoneNumberHttpService extends AbstractHttpService {

    public PhoneActivationDto phoneNumber(UserDeviceData deviceData, String phoneNumber, String userName, String userToken, RequestFormat format) {
        final UserDataCreator.TimestampTokenData token = createUserToken(userToken);

        String uri = getUri(deviceData, "PHONE_NUMBER", format);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("PHONE", phoneNumber);
        parameters.add("USER_NAME", userName);
        parameters.add("USER_TOKEN", token.getTimestampToken());
        parameters.add("TIMESTAMP", token.getTimestamp());

        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, parameters);
        String body = restTemplate.postForEntity(uri, parameters, String.class).getBody();
        logger.info("Response [{}]", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.PHONE_NUMBER_PATH, PhoneActivationDto.class);
    }

}
