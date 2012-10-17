package mobi.nowtechnologies.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TermsController extends CommonController {
	
	@RequestMapping(value="terms.html", method=RequestMethod.GET)
	public ModelAndView getTermsPage() {
		ModelAndView modelAndView = new ModelAndView("terms");
		return modelAndView ;
	}
}