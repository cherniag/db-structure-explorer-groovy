package mobi.nowtechnologies.server.shared.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class PostService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
	
	public static class Response{
		private String message;
		private int statusCode;
		
		public void setMessage(String message) {
			this.message = message;
		}
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		public String getMessage() {
			return message;
		}
		public int getStatusCode() {
			return statusCode;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
			.append("Response [message=")
			.append(message != null ? message.replaceAll("\r\n", ", ") : null)
			.append(", statusCode=")
			.append(statusCode)
			.append("]").toString();
		}
	}

	public Response sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
		if (url == null)
			throw new NullPointerException("The parameter url is null");
		
		Response response = new Response(); 
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost(url);
				
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("User-Agent", "Mozilla/4.0");
		
		try {
			if (nameValuePairs != null){
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));				
			}
			if (body != null){
				HttpEntity httpEntity = new StringEntity(body);
				
				post.setEntity(httpEntity);
				post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			}
			HttpResponse httpResponse = httpclient.execute(post);
			response.statusCode = httpResponse.getStatusLine().getStatusCode();
			HttpEntity httpEntity = httpResponse.getEntity();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				httpEntity.writeTo(byteArrayOutputStream);
				response.message = new String(byteArrayOutputStream
						.toByteArray());
			} finally {
				EntityUtils.consume(httpEntity);
				byteArrayOutputStream.close();
			}
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("post failed", e);
		}finally{
			 httpclient.getConnectionManager().shutdown();
		}
		return response;
	}
	
	
	public Response sendHttpGet(String url, Map<String, String> params) {
		if (url == null)
			throw new NullPointerException("The parameter url is null");
		
		HttpGet getMethod = new HttpGet(url);
		Response response = new Response(); 
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		try {
			if (params != null) {
				String uriParams= "";
				for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					uriParams=uriParams.concat(key).concat("=").concat(params.get(key)).concat("&");
				}
				getMethod = new HttpGet(url.concat("?").concat(uriParams));
			}

			HttpResponse httpResponse = httpclient.execute(getMethod);
			response.statusCode = httpResponse.getStatusLine().getStatusCode();
			HttpEntity httpEntity = httpResponse.getEntity();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				httpEntity.writeTo(byteArrayOutputStream);
				response.message = new String(byteArrayOutputStream
						.toByteArray());
			} finally {
				EntityUtils.consume(httpEntity);
				byteArrayOutputStream.close();
			}
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("get request failed", e);
		}finally{
			 httpclient.getConnectionManager().shutdown();
		}
		return response;
	}
	
	protected String getParamsForUri(HttpParams params) {
		String uriParams = "";
		return uriParams ;
	}
	

}
