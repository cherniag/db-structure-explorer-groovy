package mobi.nowtechnologies.applicationtests.services.http.accountcheck;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/8/2014
 */
@Service
public class AccountCheckHttpService extends AbstractHttpService {

    public AccountCheckDTO accountCheck(UserDeviceData deviceData, String userName, String storedUserToken, RequestFormat format) {
        Assert.notNull(userName);
        Assert.notNull(storedUserToken);

        UserDataCreator.TimestampTokenData token = createUserToken(storedUserToken);

        String uri = getUri(deviceData, "ACC_CHECK", format);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());

        logger.info("Posting to [" + uri + "] request: [" + request + "] for device data: [" + deviceData + "]");
        String body = restTemplate.postForEntity(uri, request, String.class).getBody();
        logger.info("Response is [{}]", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDTO.class);
    }


}
