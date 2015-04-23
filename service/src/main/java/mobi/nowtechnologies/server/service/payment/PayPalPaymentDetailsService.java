/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;

import javax.annotation.Resource;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayPalPaymentDetailsService {
    public static interface PayPalDetailsInfo {
        String getBillingAgreementDescription();

        String getSuccessUrl();

        String getFailUrl();
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    MigPaymentDetailsInfoService migPaymentDetailsInfoService;
    @Resource
    PaymentDetailsService paymentDetailsService;
    @Resource
    UserNotificationService userNotificationService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    PayPalPaymentDetailsInfoService payPalPaymentDetailsInfoService;

    private String redirectURL;
    private PayPalHttpService httpService;

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
    public void setHttpService(PayPalHttpService httpService) {
        this.httpService = httpService;
    }

    //
    // API
    //
    public String getRedirectUrl(int userId, int paymentPolicyId, PayPalDetailsInfo info) throws ServiceException {
        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(paymentPolicyId);

        if (paymentPolicy == null) {
            return null;
        }

        User user = userRepository.findOne(userId);

        PayPalResponse response = invokePayPalCallToGetRedirectUrl(info, user, paymentPolicy);

        final String redirectUrl = redirectURL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(response.getToken());

        logger.info("PayPal invocation to get redirect url was successful, redirectUrl: {}", redirectUrl);

        return redirectUrl;
    }

    public PayPalPaymentDetails commitPaymentDetails(String token, int paymentPolicyId, int userId) {
        User user = userRepository.findOne(userId);

        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(paymentPolicyId);

        logger.info("Committing PayPal payment details for user: {}", user.getId());

        PayPalResponse response = doRemoteCallToGetPaymentDetailsInfo(token, user, paymentPolicy);

        if (!response.isSuccessful()) {
            logger.error("PayPal invocation failed, description: {}", response.getDescriptionError());
            throw new ServiceException("pay.paypal.error.external", response.getDescriptionError());
        }

        PayPalPaymentDetails newPaymentDetails = payPalPaymentDetailsInfoService.commitPaymentDetails(user, paymentPolicy, response);

        logger.info("Done creation of PayPal payment details for user:{}, payment details:{}", user.getId(), newPaymentDetails.getI());

        try {
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error during sending subscription SMS after user:{} created payment details:{}", user.getId(), newPaymentDetails.getI());
        }

        return newPaymentDetails;
    }

    //
    // Internals
    //
    private PayPalResponse invokePayPalCallToGetRedirectUrl(PayPalDetailsInfo info, User user, PaymentPolicy paymentPolicy) throws ServiceException {
        String billingDescription = info.getBillingAgreementDescription();
        String successUrl = info.getSuccessUrl();
        String failUrl = info.getFailUrl();

        String communityRewriteUrlParameter = user.getUserGroup().getCommunity().getRewriteUrlParameter();

        PayPalResponse response = doRemoteCallToGetRedirectUrl(successUrl, failUrl, paymentPolicy, communityRewriteUrlParameter, billingDescription);

        if (!response.isSuccessful()) {
            logger.error("PayPal invocation failed, description: {}", response.getDescriptionError());
            throw new ServiceException("Can't connect to PayPal. Please try again later.");
        } else {
            return response;
        }
    }

    //
    // Remote calls
    //
    private PayPalResponse doRemoteCallToGetPaymentDetailsInfo(String token, User user, PaymentPolicy paymentPolicy) {
        logger.info("Invoking PayPal to get payment details info, paymentPolicy: {}, token: {}", paymentPolicy.getId(), token);

        if (PaymentPolicyType.ONETIME.equals(paymentPolicy.getPaymentPolicyType())) {
            return httpService.getPaymentDetailsInfoForOnetimeType(token, user.getUserGroup().getCommunity().getRewriteUrlParameter());
        } else {
            return httpService.getPaymentDetailsInfoForRecurrentType(token, user.getUserGroup().getCommunity().getRewriteUrlParameter());
        }
    }

    private PayPalResponse doRemoteCallToGetRedirectUrl(String successUrl, String failUrl, PaymentPolicy paymentPolicy, String communityRewriteUrlParameter, String billingDescription) {
        logger.info("Invoking PayPal to get redirect URL, paymentPolicy: {}, billingDescription:{}, successUrl:{}, failUrl:{}", paymentPolicy.getId(), billingDescription, successUrl, failUrl);

        if (PaymentPolicyType.ONETIME.equals(paymentPolicy.getPaymentPolicyType())) {
            return httpService.getTokenForOnetimeType(successUrl, failUrl, paymentPolicy.getCurrencyISO(), communityRewriteUrlParameter, paymentPolicy.getSubcost());
        } else {
            return httpService.getTokenForRecurrentType(successUrl, failUrl, paymentPolicy.getCurrencyISO(), communityRewriteUrlParameter, billingDescription);
        }
    }
}
