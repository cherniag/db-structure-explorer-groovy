package mobi.nowtechnologies.server.web.controller;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public abstract class CommonController implements MessageSourceAware {

	public static final String FAIL = "fail";
	public static final String REDIRECT = "redirect:";
	public static final String PATH_DELIM = "/";
	public static final String START_PARAM_DELIM = "?";
	public static final String PAGE_EXT = ".html";
	public static final String PAGE_VERIFY = PATH_DELIM+"verify.html";

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

	protected MessageSource messageSource;

	protected SecurityContextDetails getSecurityContextDetails() {
		return (SecurityContextDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
	}

	protected int getUserId() {
		SecurityContextDetails securityContextDetails = getSecurityContextDetails();
		int userId = securityContextDetails.getUserId();
		LOGGER.debug("Output parameter userId=[{}]", userId);
		return userId;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(value = { ValidationException.class })
	public ModelAndView handleValidationException(HttpServletRequest request, ModelAndView modelAndView, Errors errors) {
		return modelAndView;
	}
}
