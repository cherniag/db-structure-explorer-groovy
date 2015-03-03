package mobi.nowtechnologies.server.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginLogoutController {


    @RequestMapping(value = "/signin.html", method = RequestMethod.GET)
    public ModelAndView getLogin(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("signin");
        if (null != request.getParameter("error")) {
            modelAndView.addObject("errors", "");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public ModelAndView getLogout(HttpServletRequest request) {
        return new ModelAndView("signin");
    }
}