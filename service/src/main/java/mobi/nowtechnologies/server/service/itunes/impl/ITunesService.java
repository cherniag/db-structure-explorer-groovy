package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * @author Titov Mykhaylo (titov)
 */
class ITunesService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ITunesClient iTunesClient;
    private ITunesPaymentService iTunesPaymentService;
    private CommunityResourceBundleMessageSource messageSource;

    void processInAppSubscription(User user, String transactionReceipt) throws Exception {
        logger.info("Start processing ITunes subscription for user [{}], receipt [{}]", user.shortInfo(), transactionReceipt);
        if (!isEligibleForPayment(user, transactionReceipt)) {
            return;
        }
        final String actualReceipt = user.decideAppReceipt(transactionReceipt);
        final String community = user.getCommunityRewriteUrl();

        final ITunesResult result = iTunesClient.verifyReceipt(new ITunesConnectionConfig() {
            @Override
            public String getUrl() {
                return messageSource.getMessage(community, APPLE_IN_APP_I_TUNES_URL, null, null);
            }

            @Override
            public String getPassword() {
                return messageSource.getDecryptedMessage(community, APPLE_IN_APP_PASSWORD, null, null);
            }
        }, actualReceipt);

        if (result.isSuccessful()) {
            logger.info("ITunes confirmed that encoded receipt [{}] is valid by result [{}]", actualReceipt, result);
            try {
                iTunesPaymentService.createSubmittedPayment(user, actualReceipt, result);
                logger.info("Finish processing ITunes subscription");
            } catch (DataIntegrityViolationException e) {
                logger.warn("Can't process payment confirmation for user [{}], message: ", user.getId(), e.getMessage());
            }
        } else {
            logger.warn("ITunes rejected the encoded receipt [{}], result: [{}]", actualReceipt, result);
        }
    }

    private boolean isEligibleForPayment(User user, String transactionReceipt) {
        return !user.hasActivePaymentDetails() && (transactionReceipt != null || user.hasAppReceiptAndIsInLimitedState());
    }

    public void setiTunesClient(ITunesClient iTunesClient) {
        this.iTunesClient = iTunesClient;
    }

    public void setiTunesPaymentService(ITunesPaymentService iTunesPaymentService) {
        this.iTunesPaymentService = iTunesPaymentService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
