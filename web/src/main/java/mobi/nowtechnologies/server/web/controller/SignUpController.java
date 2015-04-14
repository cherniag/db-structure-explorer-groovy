package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.asm.UserRegDetailsDtoValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 */
@Controller
public class SignUpController extends CommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpController.class);

    private UserService userService;
    private CommunityService communityService;

    private RememberMeServices rememberMeServices;
    private AuthenticationManager authenticationManager;
    private UserRegDetailsDtoValidator userRegDetailsDtoValidator;

    @InitBinder(UserRegDetailsDto.USER_REG_DETAILS_DTO)
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(userRegDetailsDtoValidator);
    }

    @RequestMapping(value = "/signup.html", method = RequestMethod.GET)
    public ModelAndView getSignUpPage(HttpServletRequest request, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {

        ModelAndView modelAndView = new ModelAndView("signup");

        String communityName = communityService.getCommunityByUrl(communityUrl.getValue()).getName();

        UserRegDetailsDto userRegDetailsDto = new UserRegDetailsDto();
        userRegDetailsDto.setCommunityName(communityName);
        userRegDetailsDto.setApiVersion("V2.1");
        userRegDetailsDto.setAppVersion("CNBETA");
        if (DeviceUtils.getCurrentDevice(request).isMobile()) {
            userRegDetailsDto.setTermsConfirmed(true);
        }

        modelAndView.addObject(UserRegDetailsDto.USER_REG_DETAILS_DTO, userRegDetailsDto);

        LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/signup.html", method = RequestMethod.POST)
    public ModelAndView registerUser(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute(UserRegDetailsDto.USER_REG_DETAILS_DTO) UserRegDetailsDto userRegDetailsDto,
                                     BindingResult bindingResult) {
        LOGGER.debug("input parameters request, userRegDetailsDto, bindingResult: [{}], [{}], [{}]", new Object[] {request, userRegDetailsDto, bindingResult});

        ModelAndView modelAndView;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("signup");
            modelAndView.addObject(UserRegDetailsDto.USER_REG_DETAILS_DTO, userRegDetailsDto);
        } else {

            String remoteAddr = Utils.getIpFromRequest(request);

            userRegDetailsDto.setIpAddress(remoteAddr);

            userService.registerUser(userRegDetailsDto);

            modelAndView = new ModelAndView("redirect:getapp.html");
            autologin(request, response, userRegDetailsDto.getEmail(), userRegDetailsDto.getPassword());
        }
        LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    protected void autologin(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticated = authenticationManager.authenticate(authentication);
        rememberMeServices.loginSuccess(request, response, authenticated);
        LOGGER.debug("Logging in with {}", authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setRememberMeServices(RememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setUserRegDetailsDtoValidator(UserRegDetailsDtoValidator userRegDetailsDtoValidator) {
        this.userRegDetailsDtoValidator = userRegDetailsDtoValidator;
    }
}
