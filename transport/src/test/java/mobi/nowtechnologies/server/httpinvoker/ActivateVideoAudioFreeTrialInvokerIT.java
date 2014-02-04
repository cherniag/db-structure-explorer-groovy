package mobi.nowtechnologies.server.httpinvoker;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml"})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ActivateVideoAudioFreeTrialInvokerIT {
    @Autowired
    private ApplicationContext applicationContext;
    @Resource
    private UrlsProducer urlsProducer;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private Reporter reporter;
    @Resource
    private DataToUrlStrategy videoAudioFreeTrialUrlStrategy;

    @Test
    @Ignore
    public void testInvokeRealHttpToActivateVideoAudioFreeTrial() throws Exception {
        final String target = "localhost";

        org.springframework.core.io.Resource resource = applicationContext.getResource("classpath:testData/users-ids.txt");

        HttpHeaders httpHeaders = createXmlHttpHeaders();
        HttpEntity<String> request = new HttpEntity<String>(httpHeaders);

        Map<String,String> urlsMap = urlsProducer.produceUrls(resource, videoAudioFreeTrialUrlStrategy);
        for (Map.Entry<String, String> url : urlsMap.entrySet()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url.getValue());
            builder.host(target);
            builder.port(8080);
            builder.scheme("http");

            String endpoint = builder.build().toString();

            ResponseEntity<String> response;
            try {
                response = restTemplate.postForEntity(endpoint, request, String.class);
                reporter.report(url.getKey(), response);
            } catch (HttpServerErrorException e) {
                reporter.reportError(url.getKey(), e);
            }
        }
    }

    private HttpHeaders createXmlHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.set("Accept", "application/xml");
        return httpHeaders;
    }
}
