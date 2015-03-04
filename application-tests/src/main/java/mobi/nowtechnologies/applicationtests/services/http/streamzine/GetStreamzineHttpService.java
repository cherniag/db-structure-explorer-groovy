package mobi.nowtechnologies.applicationtests.services.http.streamzine;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GetStreamzineHttpService extends AbstractHttpService {

    public <T> ResponseEntity<T> getStreamzine(String communityUrl, UserDeviceData deviceData, PhoneState state, String timestampToken, String timestamp, String resolution, String userName,
                                               Class<T> type) {
        return doSend(communityUrl, deviceData, state, timestampToken, timestamp, resolution, userName, type, HttpMethod.GET);
    }

    public <T> ResponseEntity<T> postStreamzine(String communityUrl, UserDeviceData deviceData, PhoneState state, String timestampToken, String timestamp, String resolution, String userName,
                                                Class<T> type) {
        return doSend(communityUrl, deviceData, state, timestampToken, timestamp, resolution, userName, type, HttpMethod.POST);
    }

    private <T> ResponseEntity<T> doSend(String communityUrl, UserDeviceData deviceData, PhoneState state, String timestampToken, String timestamp, String resolution, String userName, Class<T> type,
                                         HttpMethod method) {
        String uri = getUri(communityUrl, deviceData, "GET_STREAMZINE", deviceData.getFormat());

        UriComponentsBuilder builder = createBuilderWithParameters(state, uri, resolution, timestampToken, timestamp, userName);

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        UriComponents build = builder.build();

        logger.info("\nSending for [{}] to [{}] headers [{}], parameters [{}]", deviceData, uri, headers, build.getQueryParams());
        ResponseEntity<T> entity = restTemplate.exchange(build.toUri(), method, httpEntity, type);
        logger.info("Response entity [{}]\n", entity);

        return entity;
    }

    private UriComponentsBuilder createBuilderWithParameters(PhoneState state, String uri, String resolution, String timestampToken, String timestamp, String userName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        builder.queryParam("USER_NAME", userName);
        builder.queryParam("USER_TOKEN", timestampToken);
        builder.queryParam("TIMESTAMP", timestamp);
        builder.queryParam("APP_VERSION", "1");
        builder.queryParam("WIDTHXHEIGHT", resolution);
        builder.queryParam("DEVICE_UID", state.getDeviceUID());
        return builder;
    }
}
