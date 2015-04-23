package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public class PayPalPaymentServiceImpl extends AbstractPaymentSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayPalPaymentServiceImpl.class);

    private PayPalHttpService httpService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void startPayment(PendingPayment pendingPayment) throws ServiceException {
        LOGGER.info("Start PayPal payment with internal transaction id: {}", pendingPayment.getInternalTxId());
        PayPalPaymentDetails currentPaymentDetails = pendingPayment.getUser().getCurrentPaymentDetails();
        PaymentPolicy currentPaymentPolicy = currentPaymentDetails.getPaymentPolicy();
        String communityRewriteUrlParameter = currentPaymentPolicy.getCommunity() != null ? currentPaymentPolicy.getCommunity().getRewriteUrlParameter() : null;

        PayPalResponse response;
        if (PaymentPolicyType.ONETIME.equals(currentPaymentPolicy.getPaymentPolicyType()) && !isPaymentDetailsForOnetimePaymentAndCreatedBeforeSRV648(currentPaymentDetails)) {
            response = httpService.makePaymentForOnetimeType(currentPaymentDetails.getToken(),
                                                             communityRewriteUrlParameter,
                                                             currentPaymentDetails.getPayerId(),
                                                             pendingPayment.getCurrencyISO(),
                                                             pendingPayment.getAmount());
        } else {
            response =
                httpService.makePaymentForRecurrentType(currentPaymentDetails.getBillingAgreementTxId(), pendingPayment.getCurrencyISO(), pendingPayment.getAmount(), communityRewriteUrlParameter);
        }

        pendingPayment.setExternalTxId(response.getTransactionId());
        getPendingPaymentRepository().save(pendingPayment);
        LOGGER.info("PayPal responded {} for pending payment id: {}", response, pendingPayment.getI());
        commitPayment(pendingPayment, response);
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        PayPalResponse response = new PayPalResponse(new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return "PayPal pending payment has been expired";
            }
        });
        return response;
    }

    //todo: remove this method after several weeks from release SRV-648 (It is for backward compatibility)
    private boolean isPaymentDetailsForOnetimePaymentAndCreatedBeforeSRV648(PayPalPaymentDetails paymentDetails) {
        return PaymentPolicyType.ONETIME.equals(paymentDetails.getPaymentPolicy().getPaymentPolicyType()) &&
               paymentDetails.getToken() == null &&
               paymentDetails.getPayerId() == null;
    }

    public void setHttpService(PayPalHttpService httpService) {
        this.httpService = httpService;
    }
}