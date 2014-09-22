package mobi.nowtechnologies.applicationtests.services.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * @author kots
 * @since 8/21/2014.
 */
public class LoggingResponseErrorHandler extends DefaultResponseErrorHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        logger.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
    }
}
