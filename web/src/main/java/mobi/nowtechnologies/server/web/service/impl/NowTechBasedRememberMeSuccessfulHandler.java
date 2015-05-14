package mobi.nowtechnologies.server.web.service.impl;

import mobi.nowtechnologies.server.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class NowTechBasedRememberMeSuccessfulHandler implements AuthenticationSuccessHandler {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) principal;
            int userId = userDetailsImpl.getUserId();

            String requestURI = request.getRequestURI();
            if (requestURI != null && (requestURI.endsWith("/signin") || requestURI.endsWith("/facebook_signin"))) {
                userService.updateLastWebLogin(userId);
            }
        }

    }
}