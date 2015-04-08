package mobi.nowtechnologies.server.apptests.paypal;

import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * Author: Gennadii Cherniaiev Date: 8/14/2014
 */
public class PayPalPostService extends PostService {

    @Override
    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        return getSuccessfulBasicResponse();
    }

    private BasicResponse getSuccessfulBasicResponse() {
        return new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&" +
                       "CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164&BILLINGAGREEMENTID=QWW45E98RM54S&TRANSACTIONID=4371040";
            }
        };
    }
}
