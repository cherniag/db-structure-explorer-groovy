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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsO2PsmsController extends CommonController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsController.class);
    
    
    private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyRepository paymentPolicyRepository;
    private UserRepository userRepository;
    private O2PaymentServiceImpl paymentService;

    @RequestMapping(value = {"/payments/o2psms.html"}, method = RequestMethod.GET)
    public ModelAndView createO2PaymentDetails(@RequestParam("") Short policyId){
        return new ModelAndView("payments/o2psms").addObject(PaymentsController.POLICY_REQ_PARAM, policyId);
    }

    @RequestMapping(value = {"/payments/o2psms_confirm.html"}, method = RequestMethod.GET)
    public ModelAndView getO2PsmsConfirmationPage(@RequestParam(PaymentsController.POLICY_REQ_PARAM) Short policyId) {
        LOG.info("Create o2psms payment details by paymentPolicy.id=" + policyId);

        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);

        O2PSMSPaymentDetails details = paymentService.commitPaymnetDetails(user, policy);
        user.setCurrentPaymentDetails(details);
        paymentDetailsService.update(details);
        userRepository.save(user);

        return new ModelAndView("redirect:/payments.html");
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
