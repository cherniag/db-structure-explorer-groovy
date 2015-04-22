package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.ITunesService;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesServiceImpl implements ITunesService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ITunesClient iTunesClient;
    private ITunesPaymentService iTunesPaymentService;
    private CommunityResourceBundleMessageSource messageSource;
    private ITunesPaymentDetailsService iTunesPaymentDetailsService;

    @Override
    public void processInAppSubscription(User user, String transactionReceipt, boolean createITunesPaymentDetails) throws Exception {
        logger.info("Start processing ITunes subscription for user [{}], receipt [{}], createITunesPaymentDetails [{}]",
                    user.shortInfo(), transactionReceipt, createITunesPaymentDetails);
        if (createITunesPaymentDetails) {
            if(StringUtils.isNotEmpty(transactionReceipt)) {
                iTunesPaymentDetailsService.assignAppStoreReceipt(user, transactionReceipt);
            }
        } else {
            processInternal(user, transactionReceipt);
        }
    }

    private void processInternal(final User user, String transactionReceipt) throws Exception {
        if (!shouldBeProcessedWithOldLogic(user, transactionReceipt)) {
             return;
        }
        final String actualReceipt = decideAppReceipt(transactionReceipt, user);

        logger.info("Process user with old logic");

        final ITunesResult result = iTunesClient.verifyReceipt(new ITunesConnectionConfig() {
            @Override
            public String getUrl() {
                return messageSource.getMessage(user.getCommunityRewriteUrl(), APPLE_IN_APP_I_TUNES_URL, null, null);
            }

            @Override
            public String getPassword() {
                return messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), APPLE_IN_APP_PASSWORD, null, null);
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

    private boolean shouldBeProcessedWithOldLogic(User user, String transactionReceipt) {
        return !user.hasActivePaymentDetails() && (transactionReceipt != null || user.hasAppReceiptAndIsInLimitedState());
    }

    private String decideAppReceipt(String transactionReceipt, User user) {
        String base64EncodedAppStoreReceipt = user.getBase64EncodedAppStoreReceipt();
        if (base64EncodedAppStoreReceipt != null && transactionReceipt == null) {
            return base64EncodedAppStoreReceipt;
        } else {
            return transactionReceipt;
        }
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

    public void setiTunesPaymentDetailsService(ITunesPaymentDetailsService iTunesPaymentDetailsService) {
        this.iTunesPaymentDetailsService = iTunesPaymentDetailsService;
    }
}
