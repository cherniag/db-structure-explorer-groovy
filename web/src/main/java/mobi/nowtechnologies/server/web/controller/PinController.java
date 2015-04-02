package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.common.util.PhoneData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.pincode.MaxAttemptsReachedException;
import mobi.nowtechnologies.server.service.pincode.MaxGenerationReachedException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.PinModelService;
import mobi.nowtechnologies.server.web.service.impl.PinService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PinController extends CommonController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    CommunityServiceFactory communityServiceFactory;
    @Resource
    UserRepository userRepository;
    @Resource
    PinService pinService;
    @Resource
    PinCodeService pinCodeService;

    @RequestMapping(value = {"pin/check"}, method = RequestMethod.GET)
    public ModelAndView enter() {
        logger.info("Open check pin page");

        return new ModelAndView("pin/check");
    }

    @RequestMapping(value = {"pin/resend"}, method = RequestMethod.GET)
    public ModelAndView resend(@RequestParam("phone") String phone) {
        logger.info("Resend pin for user {} and phone {}", getUserId(), phone);

        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView("pin/check");

        boolean resent = true;
        try {
            pinService.sendPinToUser(user, phone);
        } catch (MaxGenerationReachedException maxGenerationReached) {
            modelAndView.addObject("maxAttemptsReached", true);
            resent = false;
        }
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("resent", resent);
        return modelAndView;
    }

    @RequestMapping(value = {"pin/result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("pin") String pin,
                               @RequestParam("phone") String phone,
                               @RequestParam("key") String key) {
        logger.info("Open pin result page");

        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView("pin/result");

        boolean result = false;
        try {
            result = pinCodeService.attempt(user, pin);
        } catch (MaxAttemptsReachedException maxAttemptsReached) {
            modelAndView.addObject("maxAttemptsReached", true);
        }

        modelAndView.addObject("check", result);
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("key", key);

        if(result) {
            PhoneData phoneData = new PhoneData(phone);
            user.setMobile(phoneData.getMobile());
            userRepository.save(user);

            PinModelService pinModelService = getModelService(user);
            if(pinModelService != null) {
                modelAndView.addAllObjects(pinModelService.getModel(user, phone));
            }
        }

        return modelAndView;
    }



    //
    // Internal stuff
    //
    private PinModelService getModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), PinModelService.class);
    }

    private User currentUser() {
        final int userId = getUserId();
        return userRepository.findOne(userId);
    }
}
