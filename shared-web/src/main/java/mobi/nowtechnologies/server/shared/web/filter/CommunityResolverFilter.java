package mobi.nowtechnologies.server.shared.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * This filter applies only once per request and assign specific community according to
 * request parameters. Community is searched via repository and set to cookies if it's not there already.
 * @author Alexander Kolpakov (akolpakov)
 * @author dmytro
 *
 */
public class CommunityResolverFilter extends GenericFilterBean {
	
	public static final String COMMUNITY_URI_PARAM = "community";
	public static final String DEFAULT_COMMUNITY_COOKIE_NAME = "_chartsnow_community";
	static final String FILTER_APPLIED="_community_applied";
	
	public CommunityResolverFilter() {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        if (request.getAttribute(FILTER_APPLIED) != null) {
            // ensure that filter is only applied once per request
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
        
        String communityValue=request.getParameter(COMMUNITY_URI_PARAM);
        if (StringUtils.hasText(communityValue)) {
        	// check if request has community value in URI
        	
        	request = addCookie(request, response, DEFAULT_COMMUNITY_COOKIE_NAME, communityValue.toLowerCase());
        }
        
        doFilter(request, response, chain);
	}

	protected HttpServletRequest addCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String cookieName, String cookieValue) {
		Cookie[] oldCookies = httpServletRequest.getCookies();
		final List<Cookie> listCookies = new ArrayList<Cookie>();
		
		if(oldCookies != null)
		{
			for(Cookie cookie:oldCookies)
			{
				if(!cookieName.equals(cookie.getName()))
						listCookies.add(cookie);
			}
		}
		
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setPath(httpServletRequest.getContextPath());
		cookie.setMaxAge(365 * 24 * 60 * 60);
		httpServletResponse.addCookie(cookie);
		listCookies.add(cookie);
		
		return new HttpServletRequestWrapper(httpServletRequest) {
    		@Override
    		public Cookie[] getCookies() {
    			return listCookies.toArray(new Cookie[listCookies.size()]);
    		}
    	};
	}

}