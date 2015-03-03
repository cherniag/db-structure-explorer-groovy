package mobi.nowtechnologies.server.trackrepo.controller;

import javax.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// @author Alexander Kolpakov (akolpakov)
public abstract class AbstractCommonController {

    protected static final String URL_DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommonController.class);
    protected DateFormat dateFormat = new SimpleDateFormat(URL_DATE_FORMAT);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(Exception exception, HttpServletRequest request) {
        LOGGER.error("Some internal error occurred for request: [{}]", request, exception);

        ModelAndView modelAndView = new ModelAndView();
        final String errorMsg = exception.getMessage();
        modelAndView.addObject("error", errorMsg);

        return modelAndView;
    }
}