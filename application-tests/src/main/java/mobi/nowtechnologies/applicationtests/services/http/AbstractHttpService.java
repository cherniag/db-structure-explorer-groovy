package mobi.nowtechnologies.applicationtests.services.http;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

public abstract class AbstractHttpService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected UserDataCreator userDataCreator;

    @Resource(name = "mno.RestTemplate")
    protected RestTemplate restTemplate;

    @Value("${environment.url}")
    protected String environmentUrl;

    @Resource
    protected JsonHelper jsonHelper;

    protected String getUri(UserDeviceData deviceData, String commandName, RequestFormat format) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(environmentUrl);
        b.pathSegment("transport");
        b.pathSegment("service");
        b.pathSegment(deviceData.getCommunityUrl());
        b.pathSegment(deviceData.getApiVersion());
        b.pathSegment(commandName + format.getExt());
        return b.build().toUriString();
    }

    protected UserDataCreator.TimestampTokenData createUserToken(String userToken) {
        return userDataCreator.createUserToken(userToken);
    }
}
