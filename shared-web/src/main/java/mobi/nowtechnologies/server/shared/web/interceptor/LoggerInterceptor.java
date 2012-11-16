package mobi.nowtechnologies.server.shared.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class LoggerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String remoteAddr = Utils.getIpFromRequest(request);

		String userName = null;

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {

			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				UserDetails securityContextDetails = (UserDetails) principal;
				userName = securityContextDetails.getUsername();
			}
		}

		Cookie communityCookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
		String communityURL = communityCookie!=null?communityCookie.getValue():"no-community-request";

		LogUtils.putGlobalMDC(userName, communityURL, request.getRequestURI(), handler.getClass(), remoteAddr);
		return super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LogUtils.removeGlobalMDC();
		super.afterCompletion(request, response, handler, ex);
	}
}
