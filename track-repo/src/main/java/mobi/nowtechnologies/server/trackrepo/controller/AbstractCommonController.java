package mobi.nowtechnologies.server.trackrepo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public abstract class AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommonController.class);
	
	protected static final String URL_DATE_FORMAT = "yyyy-MM-dd";
	protected static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
	
	protected DateFormat dateFormat = new SimpleDateFormat(URL_DATE_FORMAT);
	protected DateFormat dateTimeFormat = new SimpleDateFormat(URL_DATE_TIME_FORMAT);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.error("input handleException, request: [{}]", request, exception);

		ModelAndView modelAndView = new ModelAndView();
		final String errorMsg = exception.getMessage();
		modelAndView.addObject("error", errorMsg);

		LOGGER.error("Output handleException: [{}]", modelAndView);
		return modelAndView;
	}
}