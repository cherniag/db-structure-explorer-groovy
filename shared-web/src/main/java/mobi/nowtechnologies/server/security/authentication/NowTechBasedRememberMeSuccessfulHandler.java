package mobi.nowtechnologies.server.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class NowTechBasedRememberMeSuccessfulHandler implements AuthenticationSuccessHandler {
	
	private UserService userService;
    private PromotionService promotionService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetailsImpl){
			UserDetailsImpl userDetailsImpl=(UserDetailsImpl)principal;
			int userId = userDetailsImpl.getUserId();
			User user = promotionService.assignPotentialPromotion(userId);
			
			String requestURI = request.getRequestURI();
			if (requestURI!=null&&(requestURI.endsWith("/signin") || requestURI.endsWith("/facebook_signin")))
				userService.updateLastWebLogin(user);
		}

	}
}