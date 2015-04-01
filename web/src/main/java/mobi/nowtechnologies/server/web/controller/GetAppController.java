package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.GetPhoneDto;
import mobi.nowtechnologies.server.web.validator.GetAppValidator;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GetAppController extends CommonController {

    public static final String VIEW_NAME = "getapp";

    private UserService userService;

    @InitBinder(GetPhoneDto.NAME)
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new GetAppValidator());
    }

    @RequestMapping(value = "getapp.html", method = RequestMethod.GET)
    public ModelAndView getAppPage() {
        ModelAndView modelAndView = new ModelAndView(VIEW_NAME);
        modelAndView.addObject(GetPhoneDto.NAME, new GetPhoneDto());
        return modelAndView;
    }

    @RequestMapping(value = "getapp.html", method = RequestMethod.POST)
    public ModelAndView senOtaLink(@Valid @ModelAttribute(GetPhoneDto.NAME) GetPhoneDto getPhoneDto, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        boolean sentStatus = false;
        if (!bindingResult.hasErrors()) {
            sentStatus = userService.sendSMSWithOTALink(getPhoneDto.getPhone(), getSecurityContextDetails().getUserId());
        }
        modelAndView.addObject("sentStatus", sentStatus);
        return modelAndView;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}