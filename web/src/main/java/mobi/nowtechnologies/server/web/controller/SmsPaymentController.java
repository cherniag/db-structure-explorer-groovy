package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
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

    @RequestMapping(value = {"smspayment/result"}, method = RequestMethod.GET)
    public ModelAndView commit(@RequestParam("id") int policyId) {
        ModelAndView modelAndView = new ModelAndView("smspayment/result");

        User user = getUser();


        if(hasAwaitingStatus(user)) {
            modelAndView.addObject("awaiting", true);
            return modelAndView;
        }

        boolean vfPaymentType = hasVodafonePaymentType(user);

        assign(policyId, user);

        if(vfPaymentType) {
            modelAndView.addObject("changed", true);
        }

        return modelAndView;
    }

    private boolean hasVodafonePaymentType(User user) {
        return user.getCurrentPaymentDetails() != null &&
                PaymentDetails.VF_PSMS_TYPE.equals(user.getCurrentPaymentDetails().getPaymentType()) &&
                user.getCurrentPaymentDetails().isActivated();
    }

    private boolean hasAwaitingStatus(User user) {
        return user.getCurrentPaymentDetails() != null && user.getCurrentPaymentDetails().getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
    }

    private void assign(int policyId, User user) {
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);
        PSMSPaymentService psmsPaymentService = communityServiceFactory.find(user.getCommunity(), PSMSPaymentService.class);
        psmsPaymentService.commitPaymentDetails(user, policy);
    }

    private User getUser() {
        return userRepository.findOne(getUserId());
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
