package mobi.nowtechnologies.server.web.security.facebook;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 */
@Deprecated
public class FacebookAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String REGISTRATION = "registration";

    private static final String FACEBOOK_ACCESS_CODE_PARAMETER_NAME = "code";

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookAuthenticationFilter.class);

    private String redirectUrl;

    private String loginRequestParameterName;

    private String passwordRequestParameterName;

    private String defaultTargetUrlForOldUser;

    private String defaultTargetUrlForNewUser;

    private CommunityResourceBundleMessageSource messageSource;

    protected FacebookAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        LOGGER.debug("input parameters request, response: [{}], [{}]", request, response);

        String clientId = messageSource.getDecryptedMessage(WebUtils.getCookie(request, DEFAULT_COMMUNITY_COOKIE_NAME).getValue(), "facebook.connect.fbAppId", null, null);
        String clientSecret = messageSource.getDecryptedMessage(WebUtils.getCookie(request, DEFAULT_COMMUNITY_COOKIE_NAME).getValue(), "facebook.connect.fbAppSecret", null, null);
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(clientId, clientSecret);
        OAuth2Operations oAuthOperations = connectionFactory.getOAuthOperations();

        String code = request.getParameter(FACEBOOK_ACCESS_CODE_PARAMETER_NAME);
        Authentication authentication = null;
        String redirectUri = getRedirectUri(request);

        if (code == null) {
            OAuth2Parameters parameters = new OAuth2Parameters();

            parameters.setRedirectUri(redirectUri);
            parameters.setScope("email");
            String buildAuthorizeUrl = oAuthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
            response.sendRedirect(buildAuthorizeUrl);
        } else {
            AccessGrant accessGrant = oAuthOperations.exchangeForAccess(code, redirectUri, null);
            Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);

            Facebook facebook = connection.getApi();
            FacebookProfile facebookProfile = facebook.userOperations().getUserProfile();

            authentication = new FacebookAuthenticationToken(facebookProfile, null);
            AuthenticationManager authenticationManager = getAuthenticationManager();
            authentication = authenticationManager.authenticate(authentication);

            final UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
            final String userName = userDetailsImpl.getUsername();
            final String password = userDetailsImpl.getPassword();

            SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler = (SimpleUrlAuthenticationSuccessHandler) getSuccessHandler();

            if (userDetailsImpl.isNewUser()) {
                simpleUrlAuthenticationSuccessHandler.setDefaultTargetUrl(defaultTargetUrlForNewUser);
            } else {
                simpleUrlAuthenticationSuccessHandler.setDefaultTargetUrl(defaultTargetUrlForOldUser);
            }

            request = new HttpServletRequestWrapper(request) {
                private Map parameterMap;

                @Override
                public Map getParameterMap() {
                    if (parameterMap == null) {
                        parameterMap = super.getParameterMap();

                        parameterMap.put(loginRequestParameterName, userName);
                        parameterMap.put(passwordRequestParameterName, password);
                    }
                    return parameterMap;
                }

                @Override
                public Enumeration getParameterNames() {
                    return Collections.enumeration(getParameterMap().keySet());
                }

                @Override
                public String[] getParameterValues(String name) {
                    return (String[]) getParameterMap().values().toArray(new String[0]);
                }

                @Override
                public String getParameter(String name) {
                    return (String) getParameterMap().get(name);
                }
            };
        }
        LOGGER.debug("Output parameter authentication=[{}]", authentication);

        return authentication;
    }

    private String getRedirectUri(HttpServletRequest request) {
        redirectUrl = messageSource.getMessage(WebUtils.getCookie(request, DEFAULT_COMMUNITY_COOKIE_NAME).getValue(), "facebook.connect.fbRedirectUrlOnWebPortal", null, "", null);

        String registrationValue = request.getParameter(REGISTRATION);

        String redirectUri = redirectUrl;
        if (registrationValue != null) {
            redirectUri = messageSource.getMessage(WebUtils.getCookie(request, DEFAULT_COMMUNITY_COOKIE_NAME).getValue(), "facebook.connect.fbRedirectUrlOnWebPortalPlusRegistration", null, "", null);
            ;
        }
        return redirectUri;
    }

    public void setLoginRequestParameterName(String loginRequestParameterName) {
        this.loginRequestParameterName = loginRequestParameterName;
    }

    public void setPasswordRequestParameterName(String passwordRequestParameterName) {
        this.passwordRequestParameterName = passwordRequestParameterName;
    }

    public void setDefaultTargetUrlForNewUser(String defaultTargetUrlForNewUser) {
        this.defaultTargetUrlForNewUser = defaultTargetUrlForNewUser;
    }

    public void setDefaultTargetUrlForOldUser(String defaultTargetUrlForOldUser) {
        this.defaultTargetUrlForOldUser = defaultTargetUrlForOldUser;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

}