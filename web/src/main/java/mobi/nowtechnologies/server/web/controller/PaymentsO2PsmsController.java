package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static mobi.nowtechnologies.server.web.controller.PaymentsController.*;

@Controller
public class PaymentsO2PsmsController extends CommonController {
	public static final String VIEW_PAYMENTS_O2PSMS = "/o2psms";
	public static final String VIEW_PAYMENTS_O2PSMS_CONFIRM = "/o2psms_confirm";
	
	public static final String PAGE_PAYMENTS_O2PSMS = SCOPE_PREFIX + VIEW_PAYMENTS_O2PSMS + PAGE_EXT;
	public static final String PAGE_PAYMENTS_O2PSMS_CONFIRM = SCOPE_PREFIX + VIEW_PAYMENTS_O2PSMS_CONFIRM + PAGE_EXT;

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsController.class);
    
    
    private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyRepository paymentPolicyRepository;
    private UserRepository userRepository;
    private O2PaymentServiceImpl paymentService;

    @RequestMapping(value = {PAGE_PAYMENTS_O2PSMS}, method = RequestMethod.GET)
    public ModelAndView createO2PaymentDetails(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(POLICY_REQ_PARAM) Integer policyId){
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);
        
        return new ModelAndView(scopePrefix+VIEW_PAYMENTS_O2PSMS)
                .addObject(POLICY_REQ_PARAM, policyId)
                .addObject("subcost", policy.getSubcost())
                .addObject("suweeks", policy.getSubweeks())
                .addObject("isVideoPaymentPolicy", policy.is4GVideoAudioSubscription());
    }

    @RequestMapping(value = {PAGE_PAYMENTS_O2PSMS_CONFIRM}, method = RequestMethod.GET)
    public ModelAndView getO2PsmsConfirmationPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(POLICY_REQ_PARAM) Integer policyId) {
        LOG.info("Create o2psms payment details by paymentPolicy.id=[{}]" , policyId);

        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        O2PSMSPaymentDetails details = paymentService.commitPaymentDetails(user, policy);
        user.setCurrentPaymentDetails(details);
        paymentDetailsService.update(details);
        userRepository.save(user);

        return new ModelAndView("redirect:/"+scopePrefix+".html");
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setPaymentService(O2PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }
}
