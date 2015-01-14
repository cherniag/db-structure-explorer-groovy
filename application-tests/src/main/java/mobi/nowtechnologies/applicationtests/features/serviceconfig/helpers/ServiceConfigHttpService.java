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
    public ResponseEntity<String> serviceConfig(UserDeviceData deviceData, String header) {
        String url = getUri(deviceData, "SERVICE_CONFIG.json", deviceData.getFormat());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<MultiValueMap> httpEntity = createHttpEntity(header);

        String uri = builder.build().toUriString();
        logger.info("Sending for [{}] to [{}] parameters: [{}]", deviceData, uri, httpEntity.getBody());
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        logger.info("Response body [{}]", responseEntity);
        return responseEntity;
    }

    private HttpEntity<MultiValueMap> createHttpEntity(String header) {
        HttpHeaders headers = new HttpHeaders();

        if(header != null) {
            headers.add("User-Agent", header);
        }

        //need to overwrite default accept headers
        headers.setAccept(Arrays.asList(MediaType.ALL));
        return new HttpEntity<MultiValueMap>(headers);
    }

}
