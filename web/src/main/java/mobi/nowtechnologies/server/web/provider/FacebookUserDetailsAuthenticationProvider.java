package mobi.nowtechnologies.server.web.provider;

import mobi.nowtechnologies.server.web.security.facebook.FacebookAuthenticationToken;
import mobi.nowtechnologies.server.web.service.impl.FacebookUserDetailsServiceImpl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.facebook.api.FacebookProfile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class FacebookUserDetailsAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookUserDetailsAuthenticationProvider.class);

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private FacebookUserDetailsServiceImpl facebookUserDetailsServiceImpl;

    public void setFacebookUserDetailsServiceImpl(FacebookUserDetailsServiceImpl facebookUserDetailsServiceImpl) {
        this.facebookUserDetailsServiceImpl = facebookUserDetailsServiceImpl;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOGGER.debug("input parameters authentication: [{}]", authentication);
        FacebookAuthenticationToken facebookAuthenticationToken = (FacebookAuthenticationToken) authentication;
        final FacebookProfile facebookProfile = facebookAuthenticationToken.getFacebookProfile();
        UserDetails userDetails = facebookUserDetailsServiceImpl.loadUserByFacebookProfile(facebookProfile);

        if (userDetails == null) {
            throw new UsernameNotFoundException("Couldn't find the user with facebook id [" + facebookProfile.getId() + "]");
        }
        Collection<? extends GrantedAuthority> grantedAuthorities = authoritiesMapper.mapAuthorities(userDetails.getAuthorities());

        facebookAuthenticationToken = new FacebookAuthenticationToken(userDetails, grantedAuthorities);
        facebookAuthenticationToken.setAuthenticated(true);
        LOGGER.debug("Output parameter facebookAuthenticationToken=[{}]", facebookAuthenticationToken);
        return facebookAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        LOGGER.debug("input parameters authentication: [{}]", authentication);
        boolean isSupport = FacebookAuthenticationToken.class.isAssignableFrom(authentication);
        LOGGER.debug("Output parameter isSupport=[{}]", isSupport);
        return isSupport;
    }

}
