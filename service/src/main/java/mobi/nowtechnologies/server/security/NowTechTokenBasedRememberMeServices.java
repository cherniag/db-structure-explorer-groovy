package mobi.nowtechnologies.server.security;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 
 * @author Mayboroda Dmytro
 *
 */
public class NowTechTokenBasedRememberMeServices extends TokenBasedRememberMeServices {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NowTechTokenBasedRememberMeServices.class);
	
	private AuthenticationSuccessHandler successfulHander;

    private final long EXPIRY_TIME_2_YEARS = new DateTime().plusYears(2).getMillis();
	
	public NowTechTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	public void setSuccessfulHander(AuthenticationSuccessHandler successfulHander) {
		this.successfulHander = successfulHander;
	}
	
	protected String retrieveUserName(Authentication authentication) {
		String userName = super.retrieveUserName(authentication);
		String encodedUserName = null;
		if (StringUtils.hasLength(userName)) {
			encodedUserName = getEncodedUserName(userName);
		}
		return encodedUserName;
    }

	@Override
	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		try {
			super.onLoginSuccess(request, response, successfulAuthentication);
			if (null != successfulHander)
				successfulHander.onAuthenticationSuccess(request, response, successfulAuthentication);
		} catch (IOException e) {
			LOGGER.error("Exception on successful authentication", e);
		} catch (ServletException e) {
			LOGGER.error("Exception on successful authentication", e);
		}
	}

	@Override
	protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		return DigestUtils.md5DigestAsHex((getEncodedUserName(username) + ":" + tokenExpiryTime + ":" + password + ":" + getKey()).getBytes());
	}
	
	/**
	 * Decodes username from cookie tokens to get it back in normal view.
	 * Encoding was done in method { makeTokenSignature}
	 */
	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
		UserDetails processAutoLoginCookie = null;
		try {
			cookieTokens[0] = getDecodedUserName(cookieTokens[0]);
			LOGGER.debug("Process auto login with cookie auth tokens: {}", cookieTokens);
			if(cookieTokens.length > 3) {
				// TODO remove this in release 3.5+
				// this hack is need during migration period, otherwise user will get InvalidCookieException
				// cause there might be more then 3 element. The 4th element is IP. It was removed because of
				// the issue with 3G/EDGE networks and dynamic-based IP addresses
				System.arraycopy(cookieTokens, 0, cookieTokens, 0, 3);
			}
			processAutoLoginCookie = super.processAutoLoginCookie(cookieTokens, request, response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new InvalidCookieException(e.getMessage());
		}
		return processAutoLoginCookie;
	}
	
	/**
	 * Locates the Spring Security remember me authentication token in the request header and returns its value.
     * The token is searched for by "Remember Me" cookie name. If there was no token found a default logic is used.
	 */
	@Override
	protected String extractRememberMeCookie(HttpServletRequest httpServletRequest) {
		String rememberMeCookie = httpServletRequest.getHeader(getCookieName());
		if (rememberMeCookie == null) {
			rememberMeCookie = super.extractRememberMeCookie(httpServletRequest);
		}
        if(rememberMeCookie == null){
            rememberMeCookie = httpServletRequest.getParameter(getCookieName());
        }
		return rememberMeCookie;
	}

	/**
	 * Returns encoded "remember me" authentication token based on username, password and expiration time in milliseconds
	 * All parameters should not be null
	 * Username will be encoded because it may contain restricted symbols for "remember me" authentication token.
	 * 
	 * @param userName - usauly represents an email of the user, but may also be a device IMEI or device mac address. MUST BE not null.
	 * @param password - user encoded password. MUST BE not null.
	 * @return encoded remeber me token
	 */
	public String getRememberMeToken(String userName, String password) {
		LOGGER.debug("input parameters userName, password: [{}], [{}]", new String[]{userName, password});

		String encodedUserName = getEncodedUserName(userName);
		
		String tokenSignature = makeTokenSignature(EXPIRY_TIME_2_YEARS, encodedUserName, password);
		
		String rememberMeToken = encodeCookie(new String[] {encodedUserName, Long.toString(EXPIRY_TIME_2_YEARS), tokenSignature});
		
		LOGGER.debug("Remember me auth token was generated {}", rememberMeToken);
		return rememberMeToken;
	}

	protected String getEncodedUserName(String userName) {
		return userName.replace(':', '|');
	}
	
	protected String getDecodedUserName(String userName) {
		return userName.replace('|', ':');
	}
}
