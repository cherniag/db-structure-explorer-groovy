package mobi.nowtechnologies.server.admin.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public abstract class AbstractCommonController implements MessageSourceAware{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommonController.class);
	
	protected static final String URL_DATE_FORMAT = "yyyy-MM-dd";
	protected static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
	
	protected DateFormat dateFormat = new SimpleDateFormat(URL_DATE_FORMAT);
	protected DateFormat dateTimeFormat = new SimpleDateFormat(URL_DATE_TIME_FORMAT);
	
	protected MessageSource messageSource;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Exception exception, HttpServletRequest request, Locale locale) {
		LOGGER.debug("input parameters exception, request: [{}], [{}]", exception, request);
		LOGGER.error(exception.getMessage(), exception);

		ModelAndView modelAndView = new ModelAndView("errors/500");

		final String message = messageSource.getMessage("error.page.defaultInternalError", null, locale);
		modelAndView.addObject("internal_error", message);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(ServiceException serviceException, HttpServletRequest request, Locale locale) {
		LOGGER.debug("input parameters serviceException, request: [{}], [{}]", serviceException, request);
		LOGGER.error(serviceException.getMessage(), serviceException);

		ModelAndView modelAndView = new ModelAndView("errors/500");
		final String errorCode = serviceException.getErrorCode();

		final String message = messageSource.getMessage(errorCode, null, locale);
		if (serviceException instanceof ExternalServiceException)
			modelAndView.addObject("external_error", message);
		else
			modelAndView.addObject("internal_error", message);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	public ModelAndView resourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
		LOGGER.error(exception.getMessage(), exception);
		return new ModelAndView();
	}

}
