package mobi.nowtechnologies.applicationtests.features.serviceconfig;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HeaderClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private String headerName;
    private String headerValue;

    public void setHeader(String name, String value) {
        this.headerName = name;
        this.headerValue = value;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add(headerName, headerValue);
        return execution.execute(request, body);
    }
}
