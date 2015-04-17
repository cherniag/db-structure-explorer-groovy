package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.impl.VFPaymentServiceImpl;
import static mobi.nowtechnologies.server.web.controller.PaymentsController.POLICY_REQ_PARAM;
import static mobi.nowtechnologies.server.web.controller.PaymentsController.SCOPE_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsController.class);

    private PaymentPolicyRepository paymentPolicyRepository;
    private UserRepository userRepository;
    private PaymentDetailsService paymentDetailsService;
    private O2PaymentServiceImpl o2PaymentService;
    private VFPaymentServiceImpl vfPaymentService;

    @RequestMapping(value = {PAGE_PAYMENTS_PSMS}, method = RequestMethod.GET)
    public ModelAndView getPsmsPage(@PathVariable("scopePrefix") String scopePrefix, @PathVariable("type") String type, @RequestParam(POLICY_REQ_PARAM) Integer policyId) {
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        return new ModelAndView(scopePrefix + "/" + type).addObject(POLICY_REQ_PARAM, policyId).addObject("subcost", policy.getSubcost()).addObject("period", policy.getPeriod())
                                                         .addObject("isVideoPaymentPolicy", policy.is4GVideoAudioSubscription());
    }

    @RequestMapping(value = {PAGE_PAYMENTS_PSMS_CONFIRM}, method = RequestMethod.GET)
    public ModelAndView getPsmsConfirmationPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(POLICY_REQ_PARAM) Integer policyId) {
        LOG.info("Create [{}] payment details by paymentPolicy.id=[{}]", new Object[] {policyId});

        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        PaymentDetails paymentDetails = createPaymentDetails(user, policy);
        paymentDetailsService.commitPaymentDetails(user, paymentDetails);

        return new ModelAndView("redirect:/" + scopePrefix + ".html");
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy policy) {
        if(user.isVFNZCommunityUser()) {
            int retriesOnError = vfPaymentService.getRetriesOnError();
            return new VFPSMSPaymentDetails(policy, user, retriesOnError);
        }

        if(user.isO2CommunityUser()) {
            int retriesOnError = o2PaymentService.getRetriesOnError();
            return new O2PSMSPaymentDetails(policy, user, retriesOnError);
        }

        throw new IllegalArgumentException("Can not create Payment Detail");
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

    public void setO2PaymentService(O2PaymentServiceImpl o2PaymentService) {
        this.o2PaymentService = o2PaymentService;
    }

    public void setVfPaymentService(VFPaymentServiceImpl vfPaymentService) {
        this.vfPaymentService = vfPaymentService;
    }
}
