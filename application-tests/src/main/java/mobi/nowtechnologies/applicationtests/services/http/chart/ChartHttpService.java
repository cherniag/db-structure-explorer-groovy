package mobi.nowtechnologies.applicationtests.services.http.chart;

import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ChartHttpService extends AbstractHttpService {
    public String getChart(String communityUrl, String apiVersion, String userName, String userToken, String timestamp, String deviceUID) {
        //
        // Build url
        //
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(environmentUrl);
        b.pathSegment("transport");
        b.pathSegment("service");
        b.pathSegment(communityUrl);
        b.pathSegment(apiVersion);
        b.pathSegment("GET_CHART.json");

        //
        // Build parameters
        //
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", userToken);
        request.add("TIMESTAMP", timestamp);
        request.add("DEVICE_UID", deviceUID);

        String uri = b.build().toUriString();

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        String body = restTemplate.postForEntity(uri, request, String.class).getBody();

        return body;
    }
}
