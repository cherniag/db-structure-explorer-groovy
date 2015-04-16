package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.event.service.EventLoggerService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.ITunesService;
import mobi.nowtechnologies.server.service.itunes.ITunesXPlayCapSubscriptionException;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Collections;

import com.google.common.base.Preconditions;
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
    private EventLoggerService eventLoggerService;

    @Override
    public int processXPlayCapSubscription(User user, String receipt) throws ITunesXPlayCapSubscriptionException {
        logger.info("Start processXPlayCapSubscription for user [{}], receipt [{}]", user.shortInfo(), receipt);

        final String community = user.getCommunityRewriteUrl();

        final ITunesResult response = iTunesClient.verifyReceipt(new ITunesConnectionConfig() {
            @Override
            public String getUrl() {
                return messageSource.getMessage(community, APPLE_IN_APP_I_TUNES_URL, null, null);
            }

            @Override
            public String getPassword() {
                return messageSource.getDecryptedMessage(community, APPLE_IN_APP_PASSWORD, null, null);
            }
        }, receipt);

        if (response.isSuccessful()) {
            logger.info("ITunes confirmed that playCap encoded receipt [{}] is valid by response [{}]", receipt, response);
            String appStoreProduceId = response.getProductId();

            String strPlayCapValue = messageSource.getMessage(community, appStoreProduceId, null, null);
            Preconditions.checkState(strPlayCapValue != null, String.format("Missing '%s' configuration in services_%s.properties", appStoreProduceId, community));

            int playCapValue = Integer.valueOf(strPlayCapValue);
            iTunesPaymentService.createXPlayCapPayment(user, receipt, response, playCapValue);

            logger.info("Finish processXPlayCapSubscription");
            return playCapValue;
        } else {
            logger.warn("ITunes rejected encoded receipt [{}], response: [{}]", receipt, response);

            int result = response.getResult();
            eventLoggerService.logXPlayCapReceiptVerified(user.getId(), user.getUuid(), receipt, result);

            ServerMessage serverMessage = new ServerMessage(result, Collections.EMPTY_MAP);
            throw new ITunesXPlayCapSubscriptionException(serverMessage);
        }
    }

    @Override
    public void processInAppSubscription(final User user, String transactionReceipt) {
        logger.info("Start processing ITunes subscription for user [{}], receipt [{}]", user.shortInfo(), transactionReceipt);

        final String actualReceipt = decideAppReceipt(transactionReceipt, user);
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
            iTunesPaymentService.createSubmittedPayment(user, actualReceipt, result, iTunesPaymentService);
            logger.info("Finish processing ITunes subscription");
        } else {
            logger.warn("ITunes rejected the encoded receipt [{}], result: [{}]", actualReceipt, result);
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

    public void setEventLoggerService(EventLoggerService eventLoggerService) {
        this.eventLoggerService = eventLoggerService;
    }
}
