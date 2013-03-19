package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.tags.Param;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
public class PaymentsO2PsmsController extends CommonController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsController.class);
    private PaymentDetailsService paymentDetailsService;
    private UserRepository userRepository;

    @RequestMapping(value = {"/payments/o2psms.html"}, method = RequestMethod.GET)
    public ModelAndView getO2PsmsConfirmationPage(@RequestParam("policyId") Integer policyId){
        LOG.info("Create o2psms payment details by paymentPolicy.id="+policyId);

        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        paymentDetailsService.createO2PsmsDetails(user, checkNotNull(policyId));
        System.out.println("Create o2psms payment details.");

        return new ModelAndView("payments/o2psms");
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }
}
