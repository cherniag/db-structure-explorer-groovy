package mobi.nowtechnologies.server.shared.web.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class RequestUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);
	
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static String getCommunityURL() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String communityURL=request.getParameter(CommunityResolverFilter.COMMUNITY_URI_PARAM);
	    if (!StringUtils.hasText(communityURL)) {
	    	Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
	    	if(cookie != null)
	    		communityURL = cookie.getValue();
	    }
		
		LOGGER.debug("Output parameter communityURL=[{}]", communityURL);
		return communityURL;
	}

	public static String getServerURL() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			URL reconstructedURL = new URL(request.getScheme(),
			        request.getServerName(),
			        request.getServerPort(), request.getContextPath());
			return reconstructedURL.toString();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
}
