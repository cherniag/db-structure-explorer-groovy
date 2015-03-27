package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.VFResponse;
import mobi.nowtechnologies.server.service.sms.BasicSMSMessageProcessor;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessor;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.VF_PSMS_TYPE;

import java.util.List;
import java.util.Set;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.SMSCDeliveryReceipt;

import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author Alexander Kolpakov
 */
public class VFPaymentServiceImpl extends BasicPSMSPaymentServiceImpl<VFPSMSPaymentDetails> implements SMSMessageProcessor<VFResponse> {

    protected VFNZSMSGatewayServiceImpl gatewayService;
    private Set<String> paymentCodes;
    private PendingPaymentService pendingPaymentService;
    private UserRepository userRepository;
    private VFResponse futureResponse = VFResponse.futureResponse();
    private BasicSMSMessageProcessor<VFResponse> smsMessageProcessor = (BasicSMSMessageProcessor<VFResponse>) new BasicSMSMessageProcessor<VFResponse>() {
        @Override
        public boolean supports(DeliverSm deliverSm) {
            return VFPaymentServiceImpl.this.supports(deliverSm);
        }

        @Override
        public void process(VFResponse data) {
            VFPaymentServiceImpl.this.process(data);
        }
    }.withMessageParser(futureResponse);

    public void setPaymentCodes(Set<String> paymentCodes) {
        this.paymentCodes = paymentCodes;
    }

    public void setGatewayService(VFNZSMSGatewayServiceImpl gatewayService) {
        this.gatewayService = gatewayService;
    }

    public void setPendingPaymentService(PendingPaymentService pendingPaymentService) {
        this.pendingPaymentService = pendingPaymentService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message) {
        final VFPSMSPaymentDetails paymentDetails = (VFPSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();

        gatewayService.send(paymentDetails.getPhoneNumber(), message, paymentPolicy.getShortCode(), SMSCDeliveryReceipt.SUCCESS_FAILURE, getExpireMillis());

        return futureResponse;
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return VFResponse.failResponse("VF PSMS pending payment has been expired");
    }

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return deliverSm.isSmscDeliveryReceipt() && paymentCodes.contains(deliverSm.getDestAddress());
    }

    @Override
    public void parserAndProcess(Object data) {
        smsMessageProcessor.parserAndProcess(data);
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public void process(VFResponse data) {
        String phoneNumber = data.getPhoneNumber();

        List<User> users = userRepository.findByMobile(phoneNumber);

        for (User user : users) {

            PendingPayment pendingPayment = getPendingPayment(user.getId(), VF_PSMS_TYPE);
            if (pendingPayment != null) {
                super.commitPayment(pendingPayment, data);
            }
        }
    }

    protected PendingPayment getPendingPayment(Integer userId, String paymentType) {
        List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(userId);
        for (PendingPayment pendingPayment : pendingPayments) {
            if (pendingPayment.getPaymentDetails().getPaymentType().equals(paymentType)) {
                return pendingPayment;
            }
        }

        return null;
    }

}