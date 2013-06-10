package mobi.nowtechnologies.server.shared.web.security.userdetails;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails, SecurityContextDetails {
		
	private static final long serialVersionUID = -5183548212551805407L;
	
	private User user;
	private List<GrantedAuthority> grantedAuthorities;
	private boolean isNewUser;

	public UserDetailsImpl(User user, boolean isNewUser) {
		this.user = user;
		this.isNewUser=isNewUser;

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
			grantedAuthorities = new ArrayList<GrantedAuthority>();
			grantedAuthorities.add(grantedAuthority);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return user.getToken();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int getUserId() {
		return user.getId();
	}

	@Override
	public String getUserMobile(){
		return user.getMobile();
	}

	@Override
	public List<GrantedAuthority> getUserAuthorities() {
		return grantedAuthorities;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	@Override
	public String toString() {
		return "UserDetailsImpl [grantedAuthorities=" + grantedAuthorities + ", isNewUser=" + isNewUser + ", getUserId()=" + getUserId() + "]";
	}
}

