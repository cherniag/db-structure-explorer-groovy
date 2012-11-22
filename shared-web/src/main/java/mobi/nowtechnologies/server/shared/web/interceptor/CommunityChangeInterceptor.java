package mobi.nowtechnologies.server.shared.web.interceptor;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class CommunityChangeInterceptor extends HandlerInterceptorAdapter {

	public static final String DEFAULT_COMMUNITY_COOKIE_NAME = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
	public static final String DEFAULT_LOCALE_PARAM_NAME = LocaleChangeInterceptor.DEFAULT_PARAM_NAME;
	public static final String DEFAULT_COMMUNITY_DELIM = "_";

	private String communityDelim = DEFAULT_COMMUNITY_DELIM;
	private String communityCookieName = DEFAULT_COMMUNITY_COOKIE_NAME;
	private String localeParamName = DEFAULT_LOCALE_PARAM_NAME;

	public String getCommunityCookieName() {
		return communityCookieName;
	}

	public void setCommunityCookieName(String communityCookieName) {
		this.communityCookieName = communityCookieName;
	}

	public String getLocaleParamName() {
		return localeParamName;
	}

	public void setLocaleParamName(String localeParamName) {
		this.localeParamName = localeParamName;
	}

	public String getCommunityDelim() {
		return communityDelim;
	}

	public void setCommunityDelim(String communityDelim) {
		this.communityDelim = communityDelim;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {

		String localeParam = request.getParameter(this.getLocaleParamName());
		Cookie communityCookie = WebUtils.getCookie(request, this.getCommunityCookieName());
		String community = communityCookie != null ? communityCookie.getValue() : "";

		Locale communityLocale = new Locale(community);
		if(localeParam != null)
		{
			Locale stdLocale = StringUtils.parseLocaleString(localeParam);
			communityLocale = new Locale(community+communityDelim+stdLocale.getLanguage(), stdLocale.getCountry(), stdLocale.getVariant());
		}
		
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}
		localeResolver.setLocale(request, response, communityLocale);

		return true;
	}
}
