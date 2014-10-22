package mobi.nowtechnologies.applicationtests.services.http.streamzine;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import mobi.nowtechnologies.applicationtests.services.http.common.standard.StandardResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Service
public class GetStreamzineHttpService extends AbstractHttpService {
    //
    // API
    //
    public ResponseEntity<StandardResponse> getStreamzineErrorEntity(UserDeviceData deviceData, PhoneState state, String timestampToken, String timestamp, String resolution, String userName) {
        return getStreamzine(deviceData.getCommunityUrl(), deviceData, state, timestampToken, timestamp, resolution, userName, StandardResponse.class);
    }

    public <T> ResponseEntity<T> getStreamzine(String communityUrl, UserDeviceData deviceData, PhoneState state, String timestampToken, String timestamp, String resolution, String userName, Class<T> type) {
        String uri = getUri(communityUrl, deviceData, "GET_STREAMZINE", deviceData.getFormat());

        UriComponentsBuilder builder = createBuilderWithParameters(state, uri, resolution, timestampToken, timestamp, userName);

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        UriComponents build = builder.build();

        logger.info("\nSending for [{}] to [{}] headers [{}], parameters [{}]", deviceData, uri, headers, build.getQueryParams());
        ResponseEntity<T> entity = restTemplate.exchange(build.toUri(), HttpMethod.POST, httpEntity, type);
        logger.info("Response entity [{}]\n", entity);

        return entity;
    }

    //
    // API with if-modified-since header (uses GET!!!)
    //
    public ResponseEntity<String> getStreamzineAnsSendIfModifiedSince(UserDeviceData deviceData, PhoneState state, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        return doSendIfModifiedSince(deviceData, state, headers, method);
    }

    public ResponseEntity<String> getStreamzineAnsSendIfModifiedSince(UserDeviceData deviceData, PhoneState state, long ifModifiedSince, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setIfModifiedSince(ifModifiedSince);

        return doSendIfModifiedSince(deviceData, state, headers, method);
    }

    public ResponseEntity<String> getStreamzineAnsSendIfModifiedSince(UserDeviceData deviceData, PhoneState state, String corruptedHeader, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Modified-Since", corruptedHeader);

        return doSendIfModifiedSince(deviceData, state, headers, method);
    }

    private ResponseEntity<String> doSendIfModifiedSince(UserDeviceData deviceData, PhoneState state, HttpHeaders headers, HttpMethod method) {
        UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

        String uri = getUri(deviceData, "GET_STREAMZINE", deviceData.getFormat());

        UriComponentsBuilder builder = createBuilderWithParameters(state, uri, "400x400", token.getTimestampToken(), token.getTimestamp(), state.getLastFacebookInfo().getUserName());

        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        UriComponents build = builder.build();

        logger.info("\nSending for [{}] to [{}] headers [{}], parameters [{}]", deviceData, uri, headers, build.getQueryParams());
        ResponseEntity<String> entity = restTemplate.exchange(build.toUri(), method, httpEntity, String.class);
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
