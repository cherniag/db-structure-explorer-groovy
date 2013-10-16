package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.VFResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.Processor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        final VFPSMSPaymentDetails paymentDetails = (VFPSMSPaymentDetails)pendingPayment.getPaymentDetails();
        final PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();

        try {
            final Lock lock = new ReentrantLock();
            final Condition receivedResponse  = lock.newCondition();
            final VFResponse response = new VFResponse();

            gatewayService.send(paymentDetails.getPhoneNumber(), "VF PSMS Payment Message", paymentPolicy.getShortCode(), new Processor<VFResponse>() {
                @Override
                public void process(VFResponse data) {
                    lock.lock();
                        receivedResponse.signal();
                    lock.unlock();
                }
            }.withMessageParser(response));

            lock.lock();
                receivedResponse.await();
            lock.unlock();

            return response;
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return VFResponse.failResponse("VF PSMS pending payment has been expired");
    }
}