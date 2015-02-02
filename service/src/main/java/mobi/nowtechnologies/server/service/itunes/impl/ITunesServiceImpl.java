package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.ITunesService;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesServiceImpl implements ITunesService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ITunesClient iTunesClient;
    private ITunesPaymentService iTunesPaymentService;
    private CommunityResourceBundleMessageSource messageSource;

    @Override
    public void processInAppSubscription(final User user, String transactionReceipt) {
        logger.info("Start processing ITunes subscription for user [{}], receipt [{}]", user, transactionReceipt);

        final String actualReceipt = decideAppReceipt(transactionReceipt, user);
        final String community = user.getCommunityRewriteUrl();

        final ITunesResult result = iTunesClient.verifyReceipt(
                new ITunesConnectionConfig() {
                    @Override
                    public String getUrl() {
                        return messageSource.getMessage(community, APPLE_IN_APP_I_TUNES_URL, null, null);
                    }

                    @Override
                    public String getPassword() {
                        return messageSource.getMessage(community, APPLE_IN_APP_PASSWORD, null, null);
                    }
                }, actualReceipt);

        if (result != null && result.isSuccessful()) {
            logger.info("ITunes confirmed that encoded receipt [{}] is valid by result [{}]", actualReceipt, result);
            iTunesPaymentService.createSubmittedPayment(user, actualReceipt, result, iTunesPaymentService);
            logger.info("Finish processing ITunes subscription");
        } else {
            logger.info("ITunes rejected the encoded receipt [{}], result: [{}]", actualReceipt, result);
        }
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

}
