package mobi.nowtechnologies.applicationtests.services.http.paypal;

import mobi.nowtechnologies.applicationtests.services.http.AbstractHttpService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class PayPalHttpService extends AbstractHttpService {

    public void pay(String communityRewriteUrl, String rememberMeToken, int paymentPolicyId, String payPalToken, String payPalSubscriptionResult) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("community", communityRewriteUrl);
        parameters.add("_REMEMBER_ME", rememberMeToken);
        parameters.add("token", payPalToken);
        parameters.add("paymentPolicyId", "" +paymentPolicyId);
        parameters.add("result", payPalSubscriptionResult);

        URI uri = getUri(parameters);
        HttpHeaders headers = createAndroidDeviceHeaders();

        logger.info("\nSending for to {}, parameters {}, headers", uri, parameters, headers);
        String body = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody();
        logger.info("Response body {}\n", body);
    }

    private URI getUri(MultiValueMap<String, String> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(environmentUrl)
                .pathSegment("web")
                .pathSegment("payments")
                .pathSegment("paypal.html");

        for (Map.Entry<String, List<String>> parameter : parameters.entrySet()) {
            builder.queryParam(parameter.getKey(), parameter.getValue().toArray());
        }
        return builder.build().toUri();
    }

    private HttpHeaders createAndroidDeviceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
        return headers;
    }
}
