package mobi.nowtechnologies.applicationtests.services.http;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.WebApplicationUriService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

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
    @Resource
    private WebApplicationUriService webApplicationUriService;

    protected String getUri(UserDeviceData deviceData, String commandName, RequestFormat format) {
        return webApplicationUriService.transport(deviceData, commandName, format);
    }

    protected String getUri(String community, UserDeviceData deviceData, String commandName, RequestFormat format) {
        return webApplicationUriService.transport(community, deviceData, commandName, format);
    }

    protected UserDataCreator.TimestampTokenData createUserToken(String userToken) {
        return userDataCreator.createUserToken(userToken);
    }
}
