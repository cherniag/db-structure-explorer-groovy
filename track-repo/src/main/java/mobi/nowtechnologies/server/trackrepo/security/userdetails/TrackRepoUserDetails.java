package mobi.nowtechnologies.server.trackrepo.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class TrackRepoUserDetails implements UserDetails{
	
	private static final long serialVersionUID = 4719727656734991234L;
	
	private String password;
	private String userName;
	private boolean isAccountNonExpired=true;
	private boolean isAccountNonLocked=true;
	private boolean isCredentialsNonExpired=true;
	private boolean isEnabled;
	private List<GrantedAuthority> grantedAuthorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAccountNonExpired(boolean isAccountNonExpired) {
		this.isAccountNonExpired = isAccountNonExpired;
	}

	public void setAccountNonLocked(boolean isAccountNonLocked) {
		this.isAccountNonLocked = isAccountNonLocked;
	}

	public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
		this.isCredentialsNonExpired = isCredentialsNonExpired;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public void setGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
		this.grantedAuthorities = grantedAuthorities;
	}

	@Override
	public String toString() {
		return "TrackRepoUserDetails [grantedAuthorities=" + grantedAuthorities + ", isAccountNonExpired=" + isAccountNonExpired + ", isAccountNonLocked="
				+ isAccountNonLocked + ", isCredentialsNonExpired=" + isCredentialsNonExpired + ", isEnabled=" + isEnabled + ", password=" + password
				+ ", userName=" + userName + "]";
	}

}
