package mobi.nowtechnologies.server.service.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public interface SecurityContextDetails {
	
	public int getUserId();
	
	public String getUsername();
	
	public List<GrantedAuthority> getUserAuthorities();

	String getUserMobile();
}