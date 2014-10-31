package mobi.nowtechnologies.applicationtests.services.http.news;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Author: Gennadii Cherniaiev
 * Date: 10/10/2014
 */
@Service
public class NewsHttpService extends AbstractHttpService {
    public <T> ResponseEntity<T> getNews(UserDeviceData deviceData, PhoneState state, Class<T> responseType){
        MultiValueMap<String, String> parameters = getParametersMultiValueMap(state);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUri(deviceData, "GET_NEWS", deviceData.getFormat()));
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue().get(0));
        }

        HttpEntity<MultiValueMap> httpEntity = getRequest(state);

        String uri = builder.build().toUriString();
        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, httpEntity.getBody());
        ResponseEntity<T> body = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);
        logger.info("Response body [{}]", body);

        return body;
    }

    public <T> ResponseEntity<T> postNews(UserDeviceData deviceData, PhoneState state, Class<T> responseType){
        String uri = getUri(deviceData, "GET_NEWS", deviceData.getFormat());
        HttpEntity<MultiValueMap> httpEntity = getRequest(state);

        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, httpEntity.getBody());
        ResponseEntity<T> body = restTemplate.postForEntity(uri, httpEntity, responseType);
        logger.info("Response body [{}]", body);

        return body;
    }

    private HttpEntity<MultiValueMap> getRequest(PhoneState state) {
        MultiValueMap<String, String> parameters = getParametersMultiValueMap(state);

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        return new HttpEntity<MultiValueMap>(parameters, headers);
    }

    private MultiValueMap<String, String> getParametersMultiValueMap(PhoneState state) {
        UserDataCreator.TimestampTokenData token = createUserToken(state);
        String userName = state.getLastAccountCheckResponse().userName;
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("USER_NAME", userName);
        parameters.add("USER_TOKEN", token.getTimestampToken());
        parameters.add("TIMESTAMP", token.getTimestamp());
        parameters.add("DEVICE_UID", state.getDeviceUID());
        return parameters;
    }
}
