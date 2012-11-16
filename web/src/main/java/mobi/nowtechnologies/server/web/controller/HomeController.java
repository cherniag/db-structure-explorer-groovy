package mobi.nowtechnologies.server.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class HomeController {
	
	@RequestMapping("/")
	public ModelAndView getHome(HttpServletRequest httpServletRequest, Model model) {
		return new ModelAndView("redirect:account.html");
	}	
}