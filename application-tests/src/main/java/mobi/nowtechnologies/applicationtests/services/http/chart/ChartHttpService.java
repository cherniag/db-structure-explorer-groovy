package mobi.nowtechnologies.applicationtests.services.http.chart;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ChartHttpService extends AbstractHttpService {

    public String getChart(String communityUrl, String apiVersion, String userName, String userToken, String timestamp, String deviceUID, RequestFormat format) {
        //
        // Build url
        //
        String uri = getUri(communityUrl, apiVersion, "GET_CHART", format);

        //
        // Build parameters
        //
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", userToken);
        request.add("TIMESTAMP", timestamp);
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        String body = restTemplate.postForEntity(uri, request, String.class).getBody();

        return body;
    }
}
