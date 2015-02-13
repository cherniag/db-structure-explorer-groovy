package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.PinModelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PinController extends CommonController {
    private CommunityServiceFactory communityServiceFactory;
    private UserRepository userRepository;

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

        final CheckResult result = doCheck(pin);
        if(result.isOk()) {
            PinModelService pinModelService = getModelService(user);
            if(pinModelService != null) {
                modelAndView.addAllObjects(pinModelService.getModel(user));
            }
        }
        modelAndView.addObject("result", result);
        return modelAndView;
    }

    public void setCommunityServiceFactory(CommunityServiceFactory communityServiceFactory) {
        this.communityServiceFactory = communityServiceFactory;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //
    // Internal staff
    //
    private PinModelService getModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), PinModelService.class);
    }

    private CheckResult doCheck(String pin) {
        CheckResult result = CheckResult.OK;
        // point to debug as for now
        return result;
    }

    private User currentUser() {
        final int userId = getUserId();
        return userRepository.findOne(userId);
    }

    public static enum CheckResult {
        OK, ERROR, EXPIRED;

        public boolean isOk() {
            return this == OK;
        }

        public boolean isError() {
            return this == ERROR;
        }
    }
}
