package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PayPalPaymentService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
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
public class PayPalPaymentServiceImpl extends AbstractPaymentSystemService implements PayPalPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayPalPaymentServiceImpl.class);

    private PayPalHttpService httpService;

    private String redirectURL;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void startPayment(PendingPayment pendingPayment) throws ServiceException {
        LOGGER.info("Start PayPal payment with internal transaction id: {}", pendingPayment.getInternalTxId());
        PayPalPaymentDetails currentPaymentDetails = (PayPalPaymentDetails) pendingPayment.getUser().getCurrentPaymentDetails();
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
    public PayPalPaymentDetails createPaymentDetails(String billingDescription, String successUrl, String failUrl, User user, PaymentPolicy paymentPolicy) throws ServiceException {
        LOGGER.info("Starting creation PayPal payment details");
        String communityRewriteUrlParameter = paymentPolicy.getCommunity() != null ? paymentPolicy.getCommunity().getRewriteUrlParameter() : null;
        PayPalResponse response;
        if (PaymentPolicyType.ONETIME.equals(paymentPolicy.getPaymentPolicyType())) {
            response = httpService.getTokenForOnetimeType(successUrl, failUrl, communityRewriteUrlParameter, paymentPolicy.getCurrencyISO(), paymentPolicy.getSubcost());
        } else {
            response = httpService.getTokenForRecurrentType(successUrl, failUrl, paymentPolicy.getCurrencyISO(), communityRewriteUrlParameter, billingDescription);
        }

        if (!response.isSuccessful()) {
            throw new ServiceException("Can't connect to PayPal. Please try again later.");
        }

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
        paymentDetails.setBillingAgreementTxId(redirectURL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(response.getToken())); // Temporary setting token to billingAgreement
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.resetMadeAttempts();
        paymentDetails.setRetriesOnError(getRetriesOnError());
        paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
        paymentDetails.setActivated(false);
        paymentDetails.setOwner(user);

        LOGGER.info("Creation PayPal payment details - REDIRECT to PayPal page");
        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PayPalPaymentDetails commitPaymentDetails(String token, User user, PaymentPolicy paymentPolicy, boolean activated) throws ServiceException {
        LOGGER.info("Committing PayPal payment details for user {} ...", user.getUserName());
        String communityRewriteUrlParameter = paymentPolicy.getCommunity() != null ? paymentPolicy.getCommunity().getRewriteUrlParameter() : null;
        PayPalResponse response;
        if (PaymentPolicyType.ONETIME.equals(paymentPolicy.getPaymentPolicyType())) {
            response = httpService.getPaymentDetailsInfoForOnetimeType(token, communityRewriteUrlParameter);
        } else {
            response = httpService.getPaymentDetailsInfoForRecurrentType(token, communityRewriteUrlParameter);
        }

        if (response.isSuccessful()) {
            LOGGER.debug("Got billing agreement {} from PayPal for user {}", response.getBillingAgreement(), user.getUserName());

            PayPalPaymentDetails newPaymentDetails = new PayPalPaymentDetails();
            newPaymentDetails.setBillingAgreementTxId(response.getBillingAgreement());
            newPaymentDetails.setToken(response.getToken());
            newPaymentDetails.setPayerId(response.getPayerId());
            newPaymentDetails.setPaymentPolicy(paymentPolicy);
            newPaymentDetails.setCreationTimestampMillis(Utils.getEpochMillis());

            newPaymentDetails = (PayPalPaymentDetails) super.commitPaymentDetails(user, newPaymentDetails);
            newPaymentDetails.setActivated(activated);

            LOGGER.info("Done creation of PayPal payment details for user {}", user.getUserName());
            return newPaymentDetails;
        }

        throw new ServiceException("pay.paypal.error.external", response.getDescriptionError());
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

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}