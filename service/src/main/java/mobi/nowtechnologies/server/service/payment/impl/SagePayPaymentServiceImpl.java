package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public class SagePayPaymentServiceImpl extends AbstractPaymentSystemService implements PaymentSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagePayPaymentServiceImpl.class);

    private SagePayHttpService httpService;

    /**
     * We send a release or repeat request to SagePay depends on lastPaymentStatus of the user If lastPaymentStatus of user equals to {@link PaymentDetailsStatus.NONE} means this user has never payed
     * before and this is his first payment. In this case we make release request other wise if last payment status of the user is equals to {@link PaymentDetailsStatus.SUCCESSFUL} the we do repeat
     * request.
     * <p/>
     * The feature of the SagePay system is that we get result of payment transaction in the response to sagepay server. so after we got this response we should make a redirect to our
     * PaymentController in order to finish process of the payment for particular user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void startPayment(PendingPayment pendingPayment) throws ServiceException {
        SagePayCreditCardPaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
        SagePayResponse response = null;
        if (!paymentDetails.getReleased()) {
            response = httpService.makeReleaseRequest(pendingPayment.getCurrencyISO(),
                                                      "Making first payment",
                                                      paymentDetails.getVPSTxId(),
                                                      paymentDetails.getVendorTxCode(),
                                                      paymentDetails.getSecurityKey(),
                                                      paymentDetails.getTxAuthNo(),
                                                      pendingPayment.getAmount());
        } else {
            response = httpService.makeRepeatRequest(pendingPayment.getCurrencyISO(),
                                                     "Making payment",
                                                     paymentDetails.getVPSTxId(),
                                                     paymentDetails.getVendorTxCode(),
                                                     paymentDetails.getSecurityKey(),
                                                     paymentDetails.getTxAuthNo(),
                                                     pendingPayment.getInternalTxId(),
                                                     pendingPayment.getAmount());
        }

        pendingPayment.setExternalTxId(response.getVPSTxId());

        LOGGER.info("SagePay responded {} for pending payment {}.", response, pendingPayment.getI());
        commitPayment(pendingPayment, response);
    }

    /**
     * Creating SubmittedPayment entity according to pending payment and a response status from external service
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) {
        SagePayCreditCardPaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
        if (response instanceof SagePayResponse && ((SagePayResponse) response).isSagePaySuccessful() && !paymentDetails.getReleased()) {
            paymentDetails.setReleased(true);
        }

        return super.commitPayment(pendingPayment, response);
    }

    public void setHttpService(SagePayHttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        SagePayResponse response = new SagePayResponse(new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return "SagePay pending payment has been expired";
            }
        });
        return response;
    }
}