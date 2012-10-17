package mobi.nowtechnologies.server.shared.web.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AfterRememberMeFilter extends GenericFilterBean {
	
	private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")))
				nowTechTokenBasedRememberMeServices.loginSuccess(httpServletRequest, httpServletResponse, authentication);
		}
		chain.doFilter(request, response);
	}

	public void setNowTechTokenBasedRememberMeServices(NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices) {
		this.nowTechTokenBasedRememberMeServices = nowTechTokenBasedRememberMeServices;
	}
}