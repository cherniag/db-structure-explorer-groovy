package mobi.nowtechnologies.server.web;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.MTVNZPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.MTVNZPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.impl.VFPaymentServiceImpl;

import javax.annotation.Resource;

public class PaymentServiceFacade {
    @Resource(name = "service.o2PaymentService")
    O2PaymentServiceImpl o2PaymentService;
    @Resource
    VFPaymentServiceImpl vfPaymentService;
    @Resource
    MTVNZPaymentSystemService mtvnzPaymentSystemService;

    @Resource(name = "service.PaymentDetailsService")
    PaymentDetailsService paymentDetailsService;

    @Resource
    UserRepository userRepository;

    public void createPaymentDetails(User user, PaymentPolicy policy) {
        PaymentDetails paymentDetails = null;

        if(user.isVFNZCommunityUser()) {
            int retriesOnError = vfPaymentService.getRetriesOnError();
            paymentDetails = new VFPSMSPaymentDetails(policy, user, retriesOnError);
        }

        if(user.isO2CommunityUser()) {
            int retriesOnError = o2PaymentService.getRetriesOnError();
            paymentDetails = new O2PSMSPaymentDetails(policy, user, retriesOnError);
        }

        if(user.isMtvNzCommunityUser()) {
            int retriesOnError = mtvnzPaymentSystemService.getRetriesOnError();
            paymentDetails = new MTVNZPSMSPaymentDetails(policy, user, retriesOnError);
        }

        if(paymentDetails == null) {
            throw new IllegalArgumentException("Can not create Payment Detail");
        }

        paymentDetailsService.commitPaymentDetails(user, paymentDetails);
    }
}
