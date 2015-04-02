package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.device.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    Logger logger = LoggerFactory.getLogger(getClass());

    CommunityServiceFactory communityServiceFactory;
    @Resource
    UserRepository userRepository;

    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        logger.info("Open check phone page");

        return new ModelAndView("phone/check");
    }

    @RequestMapping(value = {"reassign"}, method = RequestMethod.GET)
    public ModelAndView reassign() {
        logger.info("Open reassign phone page");

        return new ModelAndView("phone/reassign");
    }

    @RequestMapping(value = {"change"}, method = RequestMethod.GET)
    public ModelAndView change(@RequestParam("phone") String phone) {
        logger.info("Change the phone number for {} with new one: {}", getUserId(), phone);

        ModelAndView modelAndView = process(phone);
        modelAndView.setViewName("phone/result");
        modelAndView.addObject("reassigned", true);
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        logger.info("Open assign/reassign phone result page");

        ModelAndView modelAndView = process(phone);
        modelAndView.setViewName("phone/result");
        return modelAndView;
    }

    private ModelAndView process(String phone) {
        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("ios", doesUserPayByITunes(user));
        modelAndView.addAllObjects(findModelService(user).getModel(user, phone));
        return modelAndView;
    }

    private User currentUser() {return userRepository.findOne(getUserId());}

    private boolean doesUserPayByITunes(User user) {
        return DeviceType.IOS.equals(user.getDeviceType().getName());
    }

    private EnterPhoneModelService findModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), EnterPhoneModelService.class);
    }

    public void setCommunityServiceFactory(CommunityServiceFactory communityServiceFactory) {
        this.communityServiceFactory = communityServiceFactory;
    }
}
