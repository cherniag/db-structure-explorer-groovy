package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    @Resource
    UserRepository userRepository;

    CommunityServiceFactory communityServiceFactory;

    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        ModelAndView modelAndView = new ModelAndView("phone/check");
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        User user = userRepository.findOne(getUserId());

        EnterPhoneModelService enterPhoneModelService = communityServiceFactory.find(user.getCommunity(), EnterPhoneModelService.class);

        ModelAndView modelAndView = new ModelAndView("phone/result");
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("ios", DeviceType.IOS.equals(user.getDeviceType().getName()));
        modelAndView.addAllObjects(enterPhoneModelService.getModel(user, phone));
        return modelAndView;
    }

    public void setCommunityServiceFactory(CommunityServiceFactory communityServiceFactory) {
        this.communityServiceFactory = communityServiceFactory;
    }
}
