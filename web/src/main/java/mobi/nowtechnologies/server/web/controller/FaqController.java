package mobi.nowtechnologies.server.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class FaqController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FaqController.class);
	
	@RequestMapping(value = "/faq.html", method = RequestMethod.GET)
	public ModelAndView getFaqPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request: [{}]", request);

		ModelAndView modelAndView = new ModelAndView("faq");
		
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

}
