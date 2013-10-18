package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.VFResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;

/**
 * @author Alexander Kolpakov
 */
public class VFPaymentServiceImpl extends BasicPSMSPaymentServiceImpl<VFPSMSPaymentDetails> {

    private VFNZSMSGatewayServiceImpl gatewayService;

    protected VFPaymentServiceImpl() {
        super(VFPSMSPaymentDetails.class);
    }

    public void setGatewayService(VFNZSMSGatewayServiceImpl gatewayService) {
        this.gatewayService = gatewayService;
    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message) {
        final VFPSMSPaymentDetails paymentDetails = (VFPSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();

        gatewayService.send(paymentDetails.getPhoneNumber(), message, paymentPolicy.getShortCode());

        return new VFResponse();
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return VFResponse.failResponse("VF PSMS pending payment has been expired");
    }
}