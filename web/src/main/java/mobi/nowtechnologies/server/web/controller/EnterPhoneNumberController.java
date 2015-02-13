package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    private UserRepository userRepository;

    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        ModelAndView modelAndView = new ModelAndView("phone/check");
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        User user = userRepository.findOne(getUserId());

        CheckResult checkResult = doCheck(phone);
        ModelAndView modelAndView = new ModelAndView("phone/result");
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("result", checkResult);
        modelAndView.addObject("ios", DeviceType.IOS.equals(user.getDeviceType().getName()));
        return modelAndView;
    }

    private CheckResult doCheck(String phone) {
        CheckResult result = CheckResult.NO;
        // change here to debug/test
        return result;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static enum CheckResult {
        YES, NO, CONN_ERROR;

        public boolean isYes() {
            return this == YES;
        }

        public boolean isConnectionProblem() {
            return this == CONN_ERROR;
        }
    }

}
