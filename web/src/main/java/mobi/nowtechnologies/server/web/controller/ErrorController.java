package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class ErrorController extends CommonController{
	
	@RequestMapping(value = "error.html", method = RequestMethod.GET)
	public ModelAndView getInternaServerErrorPage(){
		ModelAndView modelAndView = new ModelAndView("errors/500");
		return modelAndView;
	}
	
	@RequestMapping(value = "page_not_found.html", method = RequestMethod.GET)
	public ModelAndView getPageNotFoundErrorPage(){
		ModelAndView modelAndView = new ModelAndView("errors/404");
		return modelAndView;
	}
	
	@RequestMapping(value = "exception.html", method = RequestMethod.GET)
	public ModelAndView getException() {
		throw new ServiceException("test.code", "This is a test Exception");
	}

}
