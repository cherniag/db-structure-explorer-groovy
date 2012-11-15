package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.EmailDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class RestorePasswordController extends CommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestorePasswordController.class);
	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@InitBinder(EmailDto.EMAIL_DTO)
	public void initBinder(WebDataBinder binder) {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = validatorFactory.usingContext().getValidator();
		binder.setValidator(new SpringValidatorAdapter(validator));
	}

	@RequestMapping(value = "/restore_password.html", method = RequestMethod.GET)
	public ModelAndView getRestorePasswordForm(HttpServletRequest request) {
		LOGGER.debug("input parameters request: [{}]", request);

		ModelAndView modelAndView = new ModelAndView("restore_password");

		EmailDto emailDto = new EmailDto();
		modelAndView.getModelMap().put(EmailDto.EMAIL_DTO, emailDto);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/restore_password.html", method = RequestMethod.POST)
	public ModelAndView restoreUserPassword(HttpServletRequest request,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRedirectURL,
			@Valid @ModelAttribute(EmailDto.EMAIL_DTO) EmailDto emailDto, BindingResult bindingResult) {

		LOGGER.debug("input parameters request, emailDto, communityRedirectURL, bindingResult: [{}], [{}], [{}], [{}]", new Object[] { request, emailDto,
				communityRedirectURL, bindingResult });

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			modelAndView = new ModelAndView("restore_password");
		} else {
			boolean isUserExsist = userService.restoreUserPassword(emailDto.getValue(), communityRedirectURL);
			if (!isUserExsist) {
				bindingResult.rejectValue("value", "restorePassword.userNotFound");
				modelAndView = new ModelAndView("restore_password");
			} else
				modelAndView = new ModelAndView("redirect:restore_password_confirmation.html");
		}

		modelAndView.getModelMap().put(EmailDto.EMAIL_DTO, emailDto);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = "/restore_password_confirmation.html", method = RequestMethod.GET)
	public ModelAndView getRestorePasswordConfirmation(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", new Object[] { request });

		ModelAndView modelAndView = new ModelAndView("restore_password_confirmation");

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
}
