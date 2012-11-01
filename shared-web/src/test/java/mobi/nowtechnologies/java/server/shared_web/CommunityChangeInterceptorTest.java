package mobi.nowtechnologies.java.server.shared_web;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.interceptor.CommunityChangeInterceptor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Ignore
public class CommunityChangeInterceptorTest {
	
	private static final String TEST_COMMUNITY_NAME = "samsung";

	private CommunityChangeInterceptor interceptor;
	
	private Object handler;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Before
	public void before() {
		handler = new Object();
		request = new HttpServletRequestWrapper(new MockHttpServletRequest()) {
    		@Override
    		public Cookie[] getCookies() {
    			return new Cookie[]{new Cookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, TEST_COMMUNITY_NAME)};
    		}
    	};
		response = new MockHttpServletResponse();
		interceptor = new CommunityChangeInterceptor();
	}
	
	@Test
	public void doInerception() throws IOException, ServletException {		
		interceptor.preHandle(request, response, handler);		
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		
		assertEquals(TEST_COMMUNITY_NAME, localeResolver.resolveLocale(request).getLanguage());
	}
}