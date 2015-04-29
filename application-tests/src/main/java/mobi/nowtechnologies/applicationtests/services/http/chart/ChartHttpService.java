package mobi.nowtechnologies.applicationtests.services.http.chart;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ChartHttpService extends AbstractHttpService {

    public ResponseEntity<ChartResponse> getChart(UserDeviceData deviceData, PhoneState state, String resolution, HttpMethod httpMethod) {
        MultiValueMap<String, String> parameters = getParametersMultiValueMap(state, resolution);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUri(deviceData, "GET_CHART", deviceData.getFormat()));
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue().get(0));
        }

        HttpEntity<MultiValueMap> httpEntity = getRequest(state, null);

        String uri = builder.build().toUriString();
        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, httpEntity.getBody());
        ResponseEntity<ChartResponse> body = restTemplate.exchange(uri, httpMethod, httpEntity, ChartResponse.class);
        logger.info("Response [{}]", body);

        return body;
    }

    private HttpEntity<MultiValueMap> getRequest(PhoneState state, String resolution) {
        MultiValueMap<String, String> parameters = getParametersMultiValueMap(state, resolution);

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        return new HttpEntity<MultiValueMap>(parameters, headers);
    }

    private MultiValueMap<String, String> getParametersMultiValueMap(PhoneState state, String resolution) {
        UserDataCreator.TimestampTokenData token = createUserToken(state);
        String userName = state.getLastAccountCheckResponse().userName;
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("USER_NAME", userName);
        parameters.add("USER_TOKEN", token.getTimestampToken());
        parameters.add("TIMESTAMP", token.getTimestamp());
        parameters.add("DEVICE_UID", state.getDeviceUID());
        if (resolution != null) {
            parameters.add("WIDTHXHEIGHT", resolution);
        }
        return parameters;
    }
}
