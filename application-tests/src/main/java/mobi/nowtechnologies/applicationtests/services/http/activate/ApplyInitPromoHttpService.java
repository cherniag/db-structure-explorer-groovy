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
public class ApplyInitPromoHttpService extends AbstractHttpService {
    public AccountCheckDTO applyInitPromo(AccountCheckDTO accountCheck, UserDeviceData deviceData, String otac, RequestFormat format){
        Assert.notNull(accountCheck);

        UserDataCreator.TimestampTokenData token = createUserToken(accountCheck.userToken);

        String uri = getUri(deviceData, "APPLY_INIT_PROMO", format);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", accountCheck.userName);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());
        request.add("OTAC_TOKEN", otac);
        if(!isEmpty(accountCheck.deviceUID)) {
            request.add("DEVICE_UID", accountCheck.deviceUID);
        }

        logger.info("Posting to [" + uri + "] request: [" + request + "] for device data: [" + deviceData + "]");
        String body = restTemplate.postForEntity(uri, request, String.class).getBody();
        logger.info("Response is [{}]", body);

        return jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDTO.class);
    }
}
