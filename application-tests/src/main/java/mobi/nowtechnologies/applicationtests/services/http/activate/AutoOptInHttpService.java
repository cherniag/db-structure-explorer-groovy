package mobi.nowtechnologies.applicationtests.services.http.activate;

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

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/8/2014
 */
@Service
public class AutoOptInHttpService extends AbstractHttpService {
    public AccountCheckDTO autoOptIn(AccountCheckDTO accountCheck, UserDeviceData deviceData, String otac, RequestFormat format){
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

        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, parameters);
        String body = restTemplate.postForEntity(uri, parameters, String.class).getBody();
        logger.info("Response is [{}]", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDTO.class);
    }
}
