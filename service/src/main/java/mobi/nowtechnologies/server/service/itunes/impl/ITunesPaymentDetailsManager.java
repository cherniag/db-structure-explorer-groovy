/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.behavior.PaymentTimeService;
import mobi.nowtechnologies.server.service.itunes.AppStoreReceiptParser;

import javax.annotation.Resource;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITunesPaymentDetailsManager {
    public interface NextRetryInfo {
        void setNextRetry(Date next);
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PaymentTimeService paymentTimeService;
    @Resource
    ITunesPaymentDetailsService iTunesPaymentDetailsService;
    @Resource
    AppStoreReceiptParser appStoreReceiptParser;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    ITunesService iTunesService;

    public void processITunesSubscription(User user, String appStoreReceipt, boolean createITunesPaymentDetails, NextRetryInfo response) {
        try {
            if (createITunesPaymentDetails) {
                if (StringUtils.isNotEmpty(appStoreReceipt)) {
                    String productId = appStoreReceiptParser.getProductId(appStoreReceipt);

                    logger.info("Assign app store receipt [{}] to user [{}]", appStoreReceipt, user.getId());

                    if(!user.hasActiveITunesPaymentDetails() || !productId.equals(user.getCurrentPaymentDetails().getPaymentPolicy().getAppStoreProductId())) {
                        logger.info("Another product id [{}] or user does not have payment details", productId);
                        iTunesPaymentDetailsService.createNewPaymentDetails(user, appStoreReceipt, productId);

                        if(user.isLimited()) {
                            Date nextRetryTimeForITunesPayment = paymentTimeService.getNextRetryTimeForITunesPayment(user, new Date());
                            if (nextRetryTimeForITunesPayment != null) {
                                response.setNextRetry(nextRetryTimeForITunesPayment);
                            }
                        }
                    } else {
                        ITunesPaymentDetails currentDetails = user.getCurrentPaymentDetails();
                        boolean justNeedToUpdateTheReceipt = !appStoreReceipt.equals(currentDetails.getAppStoreReceipt());
                        if(justNeedToUpdateTheReceipt) {
                            logger.info("Update payment details [{}] with new receipt", currentDetails.getI());
                            currentDetails.updateAppStroreReceipt(appStoreReceipt);
                            paymentDetailsRepository.save(currentDetails);
                        }
                    }
                } else {
                    // temp fix for migration
                    final String existingAppStoreReceipt = user.getBase64EncodedAppStoreReceipt();
                    if(StringUtils.isNotEmpty(existingAppStoreReceipt) && user.hasITunesSubscription() && !user.hasActiveITunesPaymentDetails()) {
                        String productId = appStoreReceiptParser.getProductId(user.getBase64EncodedAppStoreReceipt());
                        logger.info("Another product id [{}] or user does not have payment details", productId);
                        iTunesPaymentDetailsService.createNewPaymentDetails(user, existingAppStoreReceipt, productId);
                    }
                }
            } else {
                // legacy
                iTunesService.processInAppSubscription(user, appStoreReceipt);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
