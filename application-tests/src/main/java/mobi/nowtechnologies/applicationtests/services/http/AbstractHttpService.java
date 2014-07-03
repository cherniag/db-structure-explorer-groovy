package mobi.nowtechnologies.applicationtests.services.http;

import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

public abstract class AbstractHttpService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected RestTemplate restTemplate;

    @Value("${environment.url}")
    protected String environmentUrl;

    @Resource
    protected JsonHelper jsonHelper;

}
