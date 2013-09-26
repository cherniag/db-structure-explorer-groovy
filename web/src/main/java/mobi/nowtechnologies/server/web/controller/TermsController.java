package mobi.nowtechnologies.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TermsController extends CommonController {
	
	@RequestMapping(value="terms.html", method=RequestMethod.GET)
	public ModelAndView getTermsPage(@RequestParam(required=false, value="show_back_button", defaultValue="1")String showBackButton) {
		
		ModelAndView modelAndView = new ModelAndView("terms");
		
		if ( "1".equals(showBackButton) ) {
			modelAndView.addObject("showBackButton", true);
		}
		
		return modelAndView ;
	}
}