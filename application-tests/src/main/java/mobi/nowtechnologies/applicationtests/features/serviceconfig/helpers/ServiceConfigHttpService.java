package mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Service
public class ServiceConfigHttpService extends AbstractHttpService {
    public ResponseEntity<String> serviceConfigUserAgent(UserDeviceData deviceData, String header) {
        return doSend(deviceData, "User-Agent", header);
    }

    public ResponseEntity<String> serviceConfigXUserAgent(UserDeviceData deviceData, String header) {
        return doSend(deviceData, "X-User-Agent", header);
    }

    private ResponseEntity<String> doSend(UserDeviceData deviceData, String headerName, String headerValue) {
        String url = getUri(deviceData, "SERVICE_CONFIG", deviceData.getFormat());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<MultiValueMap> httpEntity = createHttpEntity(headerName, headerValue);

        String uri = builder.build().toUriString();
        logger.info("Sending for [{}] to [{}] parameters: [{}], headers: [{}]", deviceData, uri, httpEntity.getBody(), httpEntity.getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        logger.info("Response body [{}]", responseEntity);
        return responseEntity;
    }

    private HttpEntity<MultiValueMap> createHttpEntity(String headerName, String headerValue) {
        HttpHeaders headers = new HttpHeaders();

        if(headerValue != null) {
            headers.add(headerName, headerValue);
        }

        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        return new HttpEntity<>(headers);
    }

}
