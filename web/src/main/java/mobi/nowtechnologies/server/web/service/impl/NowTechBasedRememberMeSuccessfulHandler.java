package mobi.nowtechnologies.server.web.service.impl;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class NowTechBasedRememberMeSuccessfulHandler implements AuthenticationSuccessHandler {
    Logger logger = LoggerFactory.getLogger(getClass());

    private TimeService timeService;
    private UserRepository userRepository;

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) principal;
            int userId = userDetailsImpl.getUserId();

            String requestURI = request.getRequestURI();

            logger.info("Attempt to update user last web login time");

            if (requestURI != null && (requestURI.endsWith("/signin") || requestURI.endsWith("/facebook_signin"))) {
                logger.info("Attempt to update user last web login time");

                User user = userRepository.findOne(userId);
                user.setLastWebLogin(timeService.nowSeconds());
                userRepository.save(user);
            }
        }

    }
}