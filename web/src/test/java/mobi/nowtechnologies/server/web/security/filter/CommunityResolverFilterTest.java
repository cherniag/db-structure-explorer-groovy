package mobi.nowtechnologies.server.web.security.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CommunityResolverFilterTest {
	
	private static final String TEST_COMMUNITY_NAME = "cnbeta";

	private CommunityResolverFilter filter;
	
	private FilterChain chain;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	
	@Before
	public void before() {
		chain = new MockFilterChain();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filter = new CommunityResolverFilter();
	}
	
	@Test
	public void doFilterWithCommunityInRequestParamsAndNoCookies() throws IOException, ServletException {
		request.setParameter(CommunityResolverFilter.COMMUNITY_URI_PARAM, TEST_COMMUNITY_NAME);
		filter.doFilter(request, response, chain);
		
		assertEquals(TEST_COMMUNITY_NAME, response.getCookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME).getValue());
	}
	
	@Test(expected=IllegalStateException.class)
	public void doFilterTwiceSuccessfuly() throws IOException, ServletException {
		request.setParameter(CommunityResolverFilter.COMMUNITY_URI_PARAM, TEST_COMMUNITY_NAME);
		filter.doFilter(request, response, chain);
		filter.doFilter(request, response, chain);
	}
}