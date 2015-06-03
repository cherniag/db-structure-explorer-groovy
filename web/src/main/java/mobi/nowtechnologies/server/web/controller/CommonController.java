package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.exception.CanNotDeactivatePaymentDetailsException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

public abstract class CommonController implements MessageSourceAware {

    public static final String FAIL = "fail";
    public static final String REDIRECT = "redirect:";
    public static final String PATH_DELIM = "/";
    public static final String START_PARAM_DELIM = "?";
    public static final String PAGE_EXT = ".html";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected MessageSource messageSource;

    private SecurityContextDetails getSecurityContextDetails() {
        return (SecurityContextDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

	protected int getUserId() {
		SecurityContextDetails securityContextDetails = getSecurityContextDetails();
		int userId = securityContextDetails.getUserId();
		logger.debug("Output parameter userId=[{}]", userId);
		return userId;
	}

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //TODO Not valid second arg in method for spring argument resolver
    @ExceptionHandler(value = {ValidationException.class})
    public ModelAndView handleValidationException(HttpServletRequest request, ModelAndView modelAndView, Errors errors) {
        return modelAndView;
    }

    @ExceptionHandler(value = {CanNotDeactivatePaymentDetailsException.class})
    public ModelAndView handleCanNotDeactivatePaymentDetailsException(HttpServletRequest request, ServiceException exception, Locale locale) {
        logger.error(exception.getMessage(), exception);
        return new ModelAndView("errors/can_not_change_payment_options");
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception e) {
        logger.error(e.getMessage(), e);
        return new ModelAndView("errors/500");
    }

    protected String getServerURL(HttpServletRequest request) {
        try {
            URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
            return reconstructedURL.toString();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
