package mobi.nowtechnologies.server.support.http;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 */
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    public BasicResponse sendHttpPost(String url, String body) {
        return sendHttpPost(url, null, body);
    }

    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        if (url == null) {
            throw new NullPointerException("The parameter url is null");
        }

        BasicResponse response = new BasicResponse();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

        HttpPost post = new HttpPost(url);

        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        post.addHeader("User-Agent", "Mozilla/4.0");

        try {
            if (nameValuePairs != null) {
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
            if (body != null) {
                HttpEntity httpEntity = new StringEntity(body);

                post.setEntity(httpEntity);
                post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            HttpResponse httpResponse = httpclient.execute(post);
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            HttpEntity httpEntity = httpResponse.getEntity();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                httpEntity.writeTo(byteArrayOutputStream);
                response.setMessage(new String(byteArrayOutputStream.toByteArray()));
            } finally {
                EntityUtils.consume(httpEntity);
                byteArrayOutputStream.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException("post failed", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }


}
