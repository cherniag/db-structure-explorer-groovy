package mobi.nowtechnologies.server.web.security.facebook;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.facebook.api.FacebookProfile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class FacebookAuthenticationToken extends AbstractAuthenticationToken {
    private FacebookProfile facebookProfile;
    private UserDetails userDetails;

    public FacebookAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = userDetails;
    }

    public FacebookAuthenticationToken(FacebookProfile facebookProfile, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.facebookProfile = facebookProfile;
    }

    public FacebookProfile getFacebookProfile() {
        return facebookProfile;
    }

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

}
