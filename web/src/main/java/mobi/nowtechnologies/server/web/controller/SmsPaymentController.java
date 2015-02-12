package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SmsPaymentController extends CommonController {
    private UserRepository userRepository;
    private PaymentPolicyRepository paymentPolicyRepository;
    private CommunityServiceFactory communityServiceFactory;

    @RequestMapping(value = {"smspayment/result"}, method = RequestMethod.POST)
    public ModelAndView commit(@RequestParam("id") int policyId) {
        User user = userRepository.findOne(getUserId());
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        PSMSPaymentService psmsPaymentService = communityServiceFactory.find(user.getCommunity(), PSMSPaymentService.class);
        psmsPaymentService.commitPaymentDetails(user, policy);

        ModelAndView modelAndView = new ModelAndView("smspayment/result");
        return modelAndView;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setCommunityServiceFactory(CommunityServiceFactory communityServiceFactory) {
        this.communityServiceFactory = communityServiceFactory;
    }
}
