package mobi.nowtechnologies.applicationtests.services.http.referral;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.server.transport.referrals.ReferralDto;

import javax.annotation.Resource;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Author: Gennadii Cherniaiev Date: 11/25/2014
 */
@Service
public class ReferralHttpService extends AbstractHttpService {

    @Resource(name = "jacksonObjectMapper")
    private ObjectMapper objectMapper;

    public <T> ResponseEntity<T> postReferrals(UserDeviceData deviceData, PhoneState state, List<ReferralDto> referrals, Class<T> responseType) {

        URI requestUri = getUriComponentsBuilder(state, deviceData, "REFERRALS");
        HttpHeaders headers = getHttpHeaders();

        HttpEntity<String> requestEntity = new HttpEntity<String>(toJson(referrals), headers);

        logger.info("\nSending for for [{}] to [{}] headers : [{}]", deviceData, requestUri, headers);
        ResponseEntity<T> entity = restTemplate.exchange(requestUri, HttpMethod.POST, requestEntity, responseType);

        logger.info("Response: [{}]\n", entity);
        return entity;
    }

    private URI getUriComponentsBuilder(PhoneState state, UserDeviceData deviceData, String commandName) {
        UserDataCreator.TimestampTokenData token = createUserToken(state);
        String uri = getUri(deviceData, commandName, deviceData.getFormat());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        builder.queryParam("USER_TOKEN", token.getTimestampToken());
        builder.queryParam("TIMESTAMP", token.getTimestamp());
        builder.queryParam("USER_NAME", state.getLastAccountCheckResponse().userName);
        return builder.build().toUri();
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String toJson(List<ReferralDto> referrals) {
        try {
            return objectMapper.writeValueAsString(referrals);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
