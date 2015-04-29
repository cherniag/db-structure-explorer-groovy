package mobi.nowtechnologies.applicationtests.services.http.context;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
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
public class ContextHttpService extends AbstractHttpService {

    //
    // API
    //
    public ResponseEntity<String> context(UserDeviceData deviceData, PhoneState state) {
        UserDataCreator.TimestampTokenData data = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

        String uri = getUri(deviceData, "CONTEXT", deviceData.getFormat());

        UriComponentsBuilder builder = createBuilderWithParameters(state, uri, data, state.getLastSocialActivationUserName());

        HttpHeaders headers = new HttpHeaders();
        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        UriComponents build = builder.build();

        logger.info("Sending for [{}] to [{}] headers [{}], parameters [{}]", deviceData, uri, headers, build.getQueryParams());
        ResponseEntity<String> entity = restTemplate.exchange(build.toUri(), HttpMethod.GET, httpEntity, String.class);
        logger.info("Response [{}]", entity);

        return entity;
    }

    private UriComponentsBuilder createBuilderWithParameters(PhoneState state, String uri, UserDataCreator.TimestampTokenData data, String userName) {
        final String timestampToken = data.getTimestampToken();
        final String timestamp = data.getTimestamp();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        builder.queryParam("USER_NAME", userName);
        builder.queryParam("USER_TOKEN", timestampToken);
        builder.queryParam("TIMESTAMP", timestamp);
        builder.queryParam("APP_VERSION", "1");
        builder.queryParam("DEVICE_UID", state.getDeviceUID());
        return builder;
    }
}
