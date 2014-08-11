package mobi.nowtechnologies.applicationtests.services.http.chart;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ChartHttpService extends AbstractHttpService {

    public String getChart(UserDeviceData deviceData, String userName, String userToken, String deviceUID, RequestFormat format) {
        UserDataCreator.TimestampTokenData token = createUserToken(userToken);

        String uri = getUri(deviceData, "GET_CHART", format);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add("USER_NAME", userName);
        request.add("USER_TOKEN", token.getTimestampToken());
        request.add("TIMESTAMP", token.getTimestamp());
        request.add("DEVICE_UID", deviceUID);

        logger.info("Posting to [" + uri + "] request: [" + request + "]");

        String body = restTemplate.postForEntity(uri, request, String.class).getBody();

        return body;
    }
}
