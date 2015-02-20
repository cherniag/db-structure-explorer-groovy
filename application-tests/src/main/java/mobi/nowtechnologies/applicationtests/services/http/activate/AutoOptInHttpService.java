package mobi.nowtechnologies.applicationtests.services.http.activate;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/8/2014
 */
@Service
public class AutoOptInHttpService extends AbstractHttpService {
    public AccountCheckDto autoOptIn(AccountCheckDto accountCheck, UserDeviceData deviceData, String otac, RequestFormat format){
        Assert.notNull(accountCheck);

        UserDataCreator.TimestampTokenData token = createUserToken(accountCheck.userToken);

        String uri = getUri(deviceData, "AUTO_OPT_IN", format);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("USER_NAME", accountCheck.userName);
        parameters.add("USER_TOKEN", token.getTimestampToken());
        parameters.add("TIMESTAMP", token.getTimestamp());
        parameters.add("DEVICE_UID", accountCheck.deviceUID);
        if(!isEmpty(otac)) {
            parameters.add("OTAC_TOKEN", otac);
        }

        logger.info("\nSending for for [{}] to [{}] parameters: [{}]", deviceData, uri, parameters);
        ResponseEntity<String> entity = restTemplate.postForEntity(uri, parameters, String.class);
        String body = entity.getBody();
        logger.info("Response body [{}]\n", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDto.class);
    }
}
