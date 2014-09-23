package mobi.nowtechnologies.applicationtests.features.serviceconfig;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.util.LoggingResponseErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
public class ServiceConfigHttpService {
    @Value("${environment.url}")
    String environmentUrl;

    private HeaderClientHttpRequestInterceptor interceptor = new HeaderClientHttpRequestInterceptor();
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    void initIntercepter() {
        List<ClientHttpRequestInterceptor> intercepters = Lists.<ClientHttpRequestInterceptor>newArrayList(interceptor);
        restTemplate.setInterceptors(intercepters);
        restTemplate.setErrorHandler(new LoggingResponseErrorHandler());
    }

    public void setHeader(String headerName, String headerValue) {
        interceptor.setHeader(headerName, headerValue);
    }

    public ResponseEntity<String> serviceConfig(UserDeviceData deviceData) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(environmentUrl);
        b.pathSegment("transport");
        b.pathSegment("service");
        b.pathSegment(deviceData.getCommunityUrl());
        b.pathSegment(deviceData.getApiVersion());
        b.pathSegment("SERVICE_CONFIG.json");
        return restTemplate.getForEntity(b.build().toUriString(), String.class, Collections.emptyMap());
    }
}
