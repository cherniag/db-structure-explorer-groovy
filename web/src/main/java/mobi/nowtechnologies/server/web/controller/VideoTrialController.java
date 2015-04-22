package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VideoTrialController extends CommonController {
    private UserService userService;
    private UserRepository userRepository;

    @RequestMapping(value = "videotrial.html", method = RequestMethod.GET)
    public ModelAndView getVideoFreeTrial(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl, Locale locale,
                                          @RequestParam(required = false, value = "return_url", defaultValue = "account.html") String returnUrl) {
        logger.info("Get Vide Free Trial page for user id:{}", getUserId());

        ModelAndView mav = new ModelAndView("videotrial");

        mav.addObject("returnUrl", returnUrl);

        return mav;
    }

    @RequestMapping(value = "videotrial.html", method = RequestMethod.POST)
    public ModelAndView saveVideoFreeTrial(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl, Locale locale,
                                           @RequestParam(required = false, value = "return_url", defaultValue = "account.html") String returnUrl) {

        int userId = getUserId();

        logger.info("Calling saveVideoFreeTrial userId - {}", userId);

        User user = userRepository.findOne(userId);

        ModelAndView mav = new ModelAndView("videotrial/confirmation");

        mav.addObject("returnUrl", returnUrl);

        if (userService.canActivateVideoTrial(user)) {
            userService.activateVideoAudioFreeTrialAndAutoOptIn(user);
        } else {
            // free trial was already activated
            logger.warn("VideoFreeTrial was already activated for user ({}) but the page was called", userId);
            mav.addObject("hasErrors", "true");
            return mav;
        }

        logger.info("User ({}) successfully activated video free trial", userId);

        return mav;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
