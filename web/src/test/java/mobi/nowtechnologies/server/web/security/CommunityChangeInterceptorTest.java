package mobi.nowtechnologies.server.web.security;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.Assert.*;

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
                return new Cookie[] {new Cookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, TEST_COMMUNITY_NAME)};
            }
        };
        response = new MockHttpServletResponse();
        interceptor = new CommunityChangeInterceptor();
        LocaleResolver localeResolver = new CookieLocaleResolver();
        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, localeResolver);
    }

    @Test
    public void doInerception() throws IOException, ServletException {
        interceptor.preHandle(request, response, handler);
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        assertEquals(TEST_COMMUNITY_NAME, localeResolver.resolveLocale(request).getLanguage());
    }
}