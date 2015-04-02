package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.web.PaymentServiceFacade;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import mobi.nowtechnologies.server.web.model.PaymentPolicyModelService;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SmsPaymentController extends CommonController {
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    CommunityServiceFactory communityServiceFactory;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    @Resource
    PaymentServiceFacade paymentServiceFacade;

    @RequestMapping(value = {"smspayment/result"}, method = RequestMethod.GET)
    public ModelAndView commit(@RequestParam("id") int policyId) {
        ModelAndView modelAndView = new ModelAndView("smspayment/result");

        User user = getUser();

        if(hasAwaitingStatus(user)) {
            modelAndView.addObject("awaiting", true);
            return modelAndView;
        }

        if(samePolicy(user, policyId)) {
            return modelAndView;
        }

        PaymentPolicyModelService modelService = getModelService(user);

        if(modelService != null) {
            modelAndView.addAllObjects(modelService.getModel(user));
        }

        assign(policyId, user);

        return modelAndView;
    }

    private boolean samePolicy(User user, int policyId) {
        return user.getCurrentPaymentDetails() != null &&
                user.getCurrentPaymentDetails().isActivated() &&
                user.getCurrentPaymentDetails().getPaymentPolicy() != null &&
                user.getCurrentPaymentDetails().getPaymentPolicy().getId() == policyId;
    }

    private boolean hasAwaitingStatus(User user) {
        return user.getCurrentPaymentDetails() != null && user.getCurrentPaymentDetails().getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
    }

    private void assign(int policyId, User user) {
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);
        paymentServiceFacade.createPaymentDetails(user, policy);
    }

    private PaymentPolicyModelService getModelService(User user) {
        return communityServiceFactory.find(user.getCommunity(), PaymentPolicyModelService.class);
    }

    private User getUser() {
        return userRepository.findOne(getUserId());
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
