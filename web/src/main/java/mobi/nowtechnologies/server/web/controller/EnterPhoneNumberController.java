package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    private UserRepository userRepository;

    @Resource
    NZSubscriberInfoService nzSubscriberInfoService;

    @Resource
    PinCodeService pinCodeService;

    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        ModelAndView modelAndView = new ModelAndView("phone/check");
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        User user = userRepository.findOne(getUserId());

        CheckResult checkResult = doCheck(phone);
        if(checkResult.isYes()){
            try {
                pinCodeService.generate(user, 4);
            } catch (PinCodeException.MaxPinCodesReached maxPinCodesReached) {
                //show "max sms per day" limit reached
            }
        }
        ModelAndView modelAndView = new ModelAndView("phone/result");
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("result", checkResult);
        modelAndView.addObject("ios", DeviceType.IOS.equals(user.getDeviceType().getName()));
        return modelAndView;
    }

    private CheckResult doCheck(String phone) {
        try {
            if(nzSubscriberInfoService.belongs(phone)){
                return CheckResult.YES;
            } else {
                return CheckResult.NO;
            }
        } catch (SubscriberServiceException.ServiceNotAvailable e) {
            return CheckResult.CONN_ERROR;
        } catch (SubscriberServiceException.MSISDNNotFound e) {
            return CheckResult.NO;
        }
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
