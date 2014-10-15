package mobi.nowtechnologies.applicationtests.services.http.chart;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ChartHttpService extends AbstractHttpService {

    public String getChart(UserDeviceData deviceData, String userName, String userToken, String deviceUID, RequestFormat format) {
        UserDataCreator.TimestampTokenData token = createUserToken(userToken);

        String uri = getUri(deviceData, "GET_CHART", format);
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("USER_NAME", userName);
        parameters.add("USER_TOKEN", token.getTimestampToken());
        parameters.add("TIMESTAMP", token.getTimestamp());
        parameters.add("DEVICE_UID", deviceUID);

        logger.info("\nSending for for [{}] to [{}] parameters: [{}]", deviceData, uri, parameters);
        ResponseEntity<String> entity = restTemplate.postForEntity(uri, parameters, String.class);
        String body = entity.getBody();
        logger.info("Response body [{}]\n", body);

        return body;
    }
}
