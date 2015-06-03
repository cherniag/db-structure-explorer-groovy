package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.O2PSMSPaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.VFPSMSPaymentDetailsService;
import static mobi.nowtechnologies.server.web.controller.PaymentsController.POLICY_REQ_PARAM;
import static mobi.nowtechnologies.server.web.controller.PaymentsController.SCOPE_PREFIX;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsPsmsController extends CommonController {
    public static final String VIEW_PAYMENTS_PSMS = "/{type:vfpsms|oppsms}";
    public static final String VIEW_PAYMENTS_PSMS_CONFIRM = "/oppsms_confirm";

    public static final String PAGE_PAYMENTS_PSMS = SCOPE_PREFIX + VIEW_PAYMENTS_PSMS + PAGE_EXT;
    public static final String PAGE_PAYMENTS_PSMS_CONFIRM = SCOPE_PREFIX + VIEW_PAYMENTS_PSMS_CONFIRM + PAGE_EXT;

    private PaymentPolicyRepository paymentPolicyRepository;
    private UserRepository userRepository;
    private O2PSMSPaymentDetailsService o2PSMSPaymentDetailsService;
    private VFPSMSPaymentDetailsService vfpsmsPaymentDetailsService;

    @RequestMapping(value = {PAGE_PAYMENTS_PSMS}, method = RequestMethod.GET)
    public ModelAndView getPsmsPage(@PathVariable("scopePrefix") String scopePrefix, @PathVariable("type") String type, @RequestParam(POLICY_REQ_PARAM) Integer policyId) {
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        return new ModelAndView(scopePrefix + "/" + type).addObject(POLICY_REQ_PARAM, policyId).addObject("subcost", policy.getSubcost()).addObject("period", policy.getPeriod())
                                                         .addObject("isVideoPaymentPolicy", policy.is4GVideoAudioSubscription());
    }

    @RequestMapping(value = {PAGE_PAYMENTS_PSMS_CONFIRM}, method = RequestMethod.GET)
    public ModelAndView getPsmsConfirmationPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(POLICY_REQ_PARAM) Integer policyId) {
        logger.info("Create [{}] payment details by paymentPolicy.id=[{}]", new Object[] {policyId});

        User user = userRepository.findOne(getUserId());
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        createPaymentDetails(user, policy);

        return new ModelAndView("redirect:/" + scopePrefix + ".html");
    }

    private void createPaymentDetails(User user, PaymentPolicy policy) {
        if(user.isVFNZCommunityUser()) {
            vfpsmsPaymentDetailsService.createPaymentDetails(user, policy);
            return;
        }

        if(user.isO2CommunityUser()) {
            o2PSMSPaymentDetailsService.createPaymentDetails(user, policy);
            return;
        }

        throw new IllegalArgumentException("Can not create Payment Detail");
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setO2PSMSPaymentDetailsService(O2PSMSPaymentDetailsService o2PSMSPaymentDetailsService) {
        this.o2PSMSPaymentDetailsService = o2PSMSPaymentDetailsService;
    }

    public void setVfpsmsPaymentDetailsService(VFPSMSPaymentDetailsService vfpsmsPaymentDetailsService) {
        this.vfpsmsPaymentDetailsService = vfpsmsPaymentDetailsService;
    }
}
