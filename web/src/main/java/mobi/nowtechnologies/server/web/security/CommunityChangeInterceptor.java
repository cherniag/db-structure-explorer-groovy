package mobi.nowtechnologies.server.web.security;

import mobi.nowtechnologies.common.util.LocaleUtils;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

public class CommunityChangeInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {

        String localeParam = request.getParameter(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        Cookie communityCookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        String community = communityCookie != null ? communityCookie.getValue() : "";

        Locale stdLocale = null;
        if (localeParam != null) {
            stdLocale = StringUtils.parseLocaleString(localeParam);
        }
        final Locale communityLocale = LocaleUtils.buildLocale(community, stdLocale);

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
        }
        localeResolver.setLocale(request, response, communityLocale);

        return true;
    }
}
