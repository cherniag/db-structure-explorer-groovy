package mobi.nowtechnologies.applicationtests.services.http.phonenumber;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
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

    public void phoneNumber(UserDeviceData deviceData, String phoneNumber, String userName, String userToken, String timestamp, RequestFormat format){

        String uri = getUri(deviceData.getCommunityUrl(), deviceData.getApiVersion().getApiVersion(), "PHONE_NUMBER", format);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("PHONE", phoneNumber);
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", userToken);
        request.add("TIMESTAMP", timestamp);

        logger.info("Posting to [" + uri + "] request: [" + request + "] for device data: [" + deviceData + "]");

        String body = restTemplate.postForEntity(uri, request, String.class).getBody();

        logger.info("Response is [{}]", body);

        //jsonHelper.extractObjectValueByPath(body, JsonHelper.PHONE_NUMBER_PATH, AccountCheckDTO.class);

    }

}
