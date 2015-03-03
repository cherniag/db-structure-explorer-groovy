package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.service.impl.TrackReportingOptionsService;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

// @author Titov Mykhaylo (titov) on 10.11.2014.
@Controller
public class TrackReportingOptionsController {

    private Logger logger = LoggerFactory.getLogger(TrackReportingOptionsController.class);

    private TrackReportingOptionsService trackReportingOptionsService;

    public void setTrackReportingOptionsService(TrackReportingOptionsService trackReportingOptionsService) {
        this.trackReportingOptionsService = trackReportingOptionsService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ModelAndView handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        logger.trace("Bad request", methodArgumentNotValidException);
        ModelAndView modelAndView = new ModelAndView("");
        modelAndView.addObject("error", methodArgumentNotValidException.getBindingResult());
        return modelAndView;
    }

    @RequestMapping(value = "/reportingOptions", method = PUT)
    public void assignReportingOptions(@Valid @RequestBody TrackReportingOptionsDto trackReportingOptionsDto) {
        trackReportingOptionsService.assignReportingOptions(trackReportingOptionsDto);
    }
}
