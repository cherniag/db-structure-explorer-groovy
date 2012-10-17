package mobi.nowtechnologies.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * EntityController
 * 
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
public class HttpRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor
{	
	public HttpRequestResponseBodyMethodProcessor() {
		super(getDefaultMessageConverters());
	}
	
	private static List<HttpMessageConverter<?>> getDefaultMessageConverters()
	{
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new XmlAwareFormHttpMessageConverter());
		
		return messageConverters;
	}
	
	public HttpRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	@Override
	protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		return new ServletServerHttpRequest(servletRequest) {
			@Override
			public InputStream getBody() throws IOException {
				return getServletRequest().getInputStream();
			}
		};
	}
}