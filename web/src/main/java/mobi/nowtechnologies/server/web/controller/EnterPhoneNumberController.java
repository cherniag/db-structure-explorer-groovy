package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.common.util.PhoneData;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.web.PaymentServiceFacade;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("phone")
public class EnterPhoneNumberController extends CommonController {
    CommunityServiceFactory communityServiceFactory;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentServiceFacade paymentServiceFacade;

    @RequestMapping(value = {"check"}, method = RequestMethod.GET)
    public ModelAndView check() {
        return new ModelAndView("phone/check");
    }

    @RequestMapping(value = {"reassign"}, method = RequestMethod.GET)
    public ModelAndView change() {
        return new ModelAndView("phone/reassign");
    }

    @RequestMapping(value = {"change"}, method = RequestMethod.GET)
    public ModelAndView reassign(@RequestParam("phone") String phone) {
        ModelAndView modelAndView = process(phone, true);
        modelAndView.setViewName("phone/result");
        modelAndView.addObject("reassigned", true);
        return modelAndView;
    }

    @RequestMapping(value = {"result"}, method = RequestMethod.GET)
    public ModelAndView result(@RequestParam("phone") String phone) {
        ModelAndView modelAndView = process(phone, false);
        modelAndView.setViewName("phone/result");
        return modelAndView;
    }

    private ModelAndView process(String phone, boolean reassign) {
        User user = userRepository.findOne(getUserId());

        if(reassign) {
            PhoneData phoneData = new PhoneData(phone);
            user.setMobile(phoneData.getMobile());
            if(user.hasActivePaymentDetails()) {
                PaymentPolicy paymentPolicy = user.getCurrentPaymentDetails().getPaymentPolicy();
                paymentServiceFacade.createPaymentDetails(user, paymentPolicy);
            }
            userRepository.save(user);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("ios", doesUserPayByITunes(user));
        modelAndView.addAllObjects(findModelService(user).getModel(user, phone));
        return modelAndView;
    }

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
