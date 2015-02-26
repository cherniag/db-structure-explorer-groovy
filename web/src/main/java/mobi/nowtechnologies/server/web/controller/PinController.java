package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.PinModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class PinController extends CommonController {
    @Resource
    CommunityServiceFactory communityServiceFactory;
    @Resource
    UserRepository userRepository;
    @Resource
    PinCodeService pinCodeService;
    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;

    Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = {"pin/check"}, method = RequestMethod.GET)
    public ModelAndView enter() {
        return new ModelAndView("pin/check");
    }

    @RequestMapping(value = {"pin/resend"}, method = RequestMethod.GET)
    public ModelAndView resend(@RequestParam("phone") String phone) {
        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView("pin/check");

        try {
            pinCodeService.generate(user, 4);
        } catch (PinCodeException.MaxGenerationReached maxGenerationReached) {
            modelAndView.addObject("maxAttemptsReached", true);
        }
        modelAndView.addObject("phone", phone);
        return modelAndView;
    }

    @RequestMapping(value = {"pin/result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("pin") String pin,
                               @RequestParam("phone") String phone,
                               @RequestParam("key") String key) {
        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView("pin/result");

        boolean result = false;
        try {
            result = pinCodeService.attempt(user, pin);
        } catch (PinCodeException.MaxAttemptsReached maxAttemptsReached) {
            modelAndView.addObject("maxAttemptsReached", true);
        }

        modelAndView.addObject("check", result);
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("key", key);

        if(result) {
            confirm(user.getId(), phone);

            PinModelService pinModelService = getModelService(user);
            if(pinModelService != null) {
                modelAndView.addAllObjects(pinModelService.getModel(user));
            }
        }

        return modelAndView;
    }



    //
    // Internal staff
    //

    private NZSubscriberInfo confirm(int userId, String msisdn) {
        logger.info("confirm msisdn {} for {}", msisdn, userId);
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
        nzSubscriberInfo.setUserId(userId);
        return subscriberInfoRepository.saveAndFlush(nzSubscriberInfo);
    }

    private PinModelService getModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), PinModelService.class);
    }

    private User currentUser() {
        final int userId = getUserId();
        return userRepository.findOne(userId);
    }
}
