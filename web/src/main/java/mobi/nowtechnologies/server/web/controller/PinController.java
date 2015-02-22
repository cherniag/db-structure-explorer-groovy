package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.PinModelService;
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

    @RequestMapping(value = {"pin/check"}, method = RequestMethod.GET)
    public ModelAndView enter() {
        ModelAndView modelAndView = new ModelAndView("pin/check");
        return modelAndView;
    }

    @RequestMapping(value = {"pin/resend"}, method = RequestMethod.GET)
    public ModelAndView resend() {
        ModelAndView modelAndView = new ModelAndView("pin/check");
        return modelAndView;
    }

    @RequestMapping(value = {"pin/result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("pin") String pin) {
        User user = currentUser();

        ModelAndView modelAndView = new ModelAndView("pin/result");

        final CheckResult result = doCheck(user, pin);
        if(result.isOk()) {
            PinModelService pinModelService = getModelService(user);
            if(pinModelService != null) {
                modelAndView.addAllObjects(pinModelService.getModel(user));
            }
        }
        modelAndView.addObject("result", result);
        return modelAndView;
    }

    //
    // Internal staff
    //
    private PinModelService getModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), PinModelService.class);
    }

    private CheckResult doCheck(User user, String pin) {
        try {
            if(pinCodeService.check(user, pin)){
                return CheckResult.OK;
            } else {
                return CheckResult.INVALID;
            }
        } catch (PinCodeException.NotFound notFound) {
            return CheckResult.INVALID;
        } catch (PinCodeException.MaxAttemptsReached maxAttemptsReached) {
            return CheckResult.MAX_ATTEMPTS;
        }
    }

    private User currentUser() {
        final int userId = getUserId();
        return userRepository.findOne(userId);
    }

    public static enum CheckResult {
        OK, MAX_ATTEMPTS, INVALID;

        public boolean isOk() {
            return this == OK;
        }

        public boolean isMaxAttempts() {
            return this == MAX_ATTEMPTS;
        }
    }
}
