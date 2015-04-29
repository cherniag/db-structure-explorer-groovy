package mobi.nowtechnologies.applicationtests.services.http.accountcheck;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.applicationtests.services.http.ResponseWrapper;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Author: Gennadii Cherniaiev Date: 7/8/2014
 */
@Service
public class AccountCheckHttpService extends AbstractHttpService {

    //
    // API
    //
    public ResponseWrapper<AccountCheckDto> accountCheck(UserDeviceData deviceData, String userName, String storedUserToken, RequestFormat format) {
        Assert.hasText(userName);
        Assert.hasText(storedUserToken);

        MultiValueMap<String, String> parameters = createCommonParameters(userName, storedUserToken);

        return execute(parameters, deviceData, format);
    }

    public ResponseWrapper<AccountCheckDto> accountCheckFromIOS(UserDeviceData deviceData, String userName, String storedUserToken, RequestFormat format, String iTunesReceipt) {
        Assert.hasText(userName);
        Assert.hasText(storedUserToken);

        MultiValueMap<String, String> parameters = createCommonParameters(userName, storedUserToken);
        parameters.add("TRANSACTION_RECEIPT", iTunesReceipt);

        return execute(parameters, deviceData, format);
    }

    public ResponseWrapper<AccountCheckDto> accountCheckWithUrbanAirshipToken(UserDeviceData deviceData, String userName, String storedUserToken, RequestFormat format, String urbanAirshipToken) {
        Assert.hasText(userName);
        Assert.hasText(storedUserToken);

        MultiValueMap<String, String> parameters = createCommonParameters(userName, storedUserToken);
        parameters.add("UA_TOKEN", urbanAirshipToken);

        return execute(parameters, deviceData, format);
    }

    //
    // Internals
    //
    private MultiValueMap<String, String> createCommonParameters(String userName, String storedUserToken) {
        UserDataCreator.TimestampTokenData token = createUserToken(storedUserToken);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());
        return request;
    }

    private ResponseWrapper<AccountCheckDto> execute(MultiValueMap<String, String> parameters, UserDeviceData deviceData, RequestFormat format) {
        String uri = getUri(deviceData, "ACC_CHECK", format);

        logger.info("Sending for [{}] to [{}] parameters [{}]", deviceData, uri, parameters);
        ResponseEntity<String> entity = restTemplate.postForEntity(uri, parameters, String.class);
        String body = entity.getBody();
        logger.info("Response [{}]", entity);

        AccountCheckDto accountCheckDto = jsonHelper.extractObjectValueByPath(body, JsonHelper.USER_PATH, AccountCheckDto.class);
        ResponseWrapper<AccountCheckDto> accountCheckDtoResponseWrapper = new ResponseWrapper<>(entity.getStatusCode().value(), entity.getHeaders(), accountCheckDto);
        return accountCheckDtoResponseWrapper;
    }


}
