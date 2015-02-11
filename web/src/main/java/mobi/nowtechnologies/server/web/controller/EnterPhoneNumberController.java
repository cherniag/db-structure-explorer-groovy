package mobi.nowtechnologies.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        ModelAndView modelAndView = new ModelAndView("phone/check");
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        ModelAndView modelAndView = new ModelAndView("phone/result");
        modelAndView.addObject("phone", phone);
        return modelAndView;
    }

}
