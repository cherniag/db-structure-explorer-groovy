package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class VFPaymentServiceImpl extends BasicPSMSPaymentServiceImpl<VFPSMSPaymentDetails>{

	private VFNZSMSGatewayServiceImpl gatewayService;

    protected VFPaymentServiceImpl() {
        super(VFPSMSPaymentDetails.class);
    }

    public void setGatewayService(VFNZSMSGatewayServiceImpl gatewayService) {
        this.gatewayService = gatewayService;
    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}