package mobi.nowtechnologies.server.web.security.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 */
public class DigestAuthenticationByCookieFilter extends DigestAuthenticationFilter {

    public static final String USER_NAME = "userName";
    public static final String REALM = "realm";
    public static final String NONCE = "nonce";

    private UserCache userCache = new NullUserCache();

    private UserDetailsService userDetailsService;

    private static String md5Hex(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(data.getBytes())));
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    public void setAuthenticationEntryPoint(DigestAuthenticationEntryPoint authenticationEntryPoint) {
        if (authenticationEntryPoint instanceof DigestAuthenticationByCookieEntryPoint) {
            super.setAuthenticationEntryPoint(authenticationEntryPoint);
        } else {
            throw new IllegalArgumentException("The " + DigestAuthenticationByCookieFilter.class.getName() + " class can work with DigestAuthenticationByCookieEntryPoint class only");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        Cookie userNameCookie = WebUtils.getCookie(httpServletRequest, USER_NAME);
        Cookie nonceCookie = WebUtils.getCookie(httpServletRequest, NONCE);
        Cookie realmCookie = WebUtils.getCookie(httpServletRequest, REALM);

        if (userNameCookie != null && nonceCookie != null) {
            HeaderServletRequestWrapper headerServletRequestWrapper = new HeaderServletRequestWrapper(httpServletRequest);
            final String userName = userNameCookie.getValue();

            UserDetails userDetails = userCache.getUserFromCache(userName);
            if (userDetails == null) {
                userDetails = userDetailsService.loadUserByUsername(userName);
            }
            String requestURI = "";
            String response = md5Hex(httpServletRequest.getMethod() + ":" + requestURI);
            String value = "Digest username='" + userName + "' realm='" + realmCookie.getValue() + "' nonce='" + nonceCookie.getValue() + "' uri='" + requestURI + "' response='" + response + "'";
            headerServletRequestWrapper.addHeader("Authorization", value);

            super.doFilter(headerServletRequestWrapper, servletResponse, chain);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // HttpServletResponse httpServletResponse =
                // (HttpServletResponse)servletResponse;
                chain.doFilter(servletRequest, servletResponse);
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private static class DigestAuthenticationByCookieEntryPoint extends DigestAuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            return;
        }
    }

    private static class HeaderServletRequestWrapper extends HttpServletRequestWrapper {

        private Map<String, String> headerMap;

        @SuppressWarnings("unchecked")
        public HeaderServletRequestWrapper(HttpServletRequest httpServletRequest) {
            super(httpServletRequest);
            headerMap = new HashMap<String, String>();

            for (Enumeration<String> enumeration = httpServletRequest.getHeaderNames(); enumeration.hasMoreElements(); ) {
                String headerName = enumeration.nextElement();
                headerMap.put(headerName, httpServletRequest.getHeader(headerName));
            }
        }

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(headerMap.keySet());
        }

        public String getHeader(String name) {
            String value;
            if ((value = headerMap.get(name)) != null) {
                return value;
            } else {
                return ((HttpServletRequest) getRequest()).getHeader(name);
            }

        }
    }

}
