package mobi.nowtechnologies.server.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class SignInController extends AbstractCommonController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SignInController.class);
	
	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public ModelAndView getSignInPage() {
		ModelAndView modelAndView = new ModelAndView("signin");
		LOGGER.info("output: {}", modelAndView);
		return modelAndView;
	}

}
