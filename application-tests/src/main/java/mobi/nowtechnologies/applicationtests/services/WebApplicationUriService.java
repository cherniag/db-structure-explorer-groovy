package mobi.nowtechnologies.applicationtests.services;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WebApplicationUriService {

    @Value("${environment.url}")
    private String environmentUrl;

    public String web(UserDeviceData deviceData, PhoneState phoneState, String path) {
        return web(deviceData, phoneState, path, Collections.<String, String>emptyMap());
    }

    public String web(UserDeviceData deviceData, PhoneState phoneState, String path, Map<String, String> parameters) {
        Assert.hasText(path, "Not valid path for web: " + path);

        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(environmentUrl);
        b.pathSegment("web");
        b.pathSegment(path + ".html");
        b.queryParam("_REMEMBER_ME", phoneState.getLastAccountCheckResponse().rememberMeToken);
        b.queryParam("community", deviceData.getCommunityUrl());

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            b.queryParam(entry.getKey(), entry.getValue());
        }

        return b.build().toUriString();
    }

    public String transport(UserDeviceData deviceData, String commandName, RequestFormat format) {
        return transport(deviceData.getCommunityUrl(), deviceData, commandName, format);
    }

    public String transport(String communityUrl, UserDeviceData deviceData, String commandName, RequestFormat format) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(environmentUrl);
        b.pathSegment("transport");
        b.pathSegment("service");
        b.pathSegment(communityUrl);
        b.pathSegment(deviceData.getApiVersion());
        b.pathSegment(commandName + format.getExt());
        return b.build().toUriString();
    }
}
