package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionException;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseFormatException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.ITunesPaymentSystemServiceHelper;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Author: Gennadii Cherniaiev Date: 4/14/2015
 */
public class ITunesPaymentSystemService extends AbstractPaymentSystemService {
    private final Logger logger = LoggerFactory.getLogger(AbstractPaymentSystemService.class);

    static final String COULDN_T_CONNECT_TO_APP_STORE = "Couldn't connect to AppStore";
    static final String UNKNOWN_APP_STORE_RESPONSE_FORMAT = "Unknown AppStore response format";
    private ITunesClient iTunesClient;
    private CommunityResourceBundleMessageSource messageSource;
    private ITunesPaymentSystemServiceHelper helper;

    @Override
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        logger.info("Start processing PendingPayment [{}]", pendingPayment);
        final ITunesPaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
        final String actualReceipt = paymentDetails.getAppStroreReceipt();

        final User owner = paymentDetails.getOwner();

        try {
            ITunesResult result = iTunesClient.verifyReceipt(getConnectionConfig(owner.getCommunityRewriteUrl()), actualReceipt);
            if(result.isSuccessful()) {
                helper.confirmPayment(pendingPayment, result);
            } else {
                String descriptionError = "Not valid receipt, status " + result.getResult();
                helper.stopSubscription(pendingPayment, descriptionError);
            }
        } catch (ITunesConnectionException e) {
            logger.error(e.getMessage(), e);
            helper.failAttempt(pendingPayment, COULDN_T_CONNECT_TO_APP_STORE);
        } catch (ITunesResponseFormatException e) {
            logger.error(e.getMessage(), e);
            helper.stopSubscription(pendingPayment, UNKNOWN_APP_STORE_RESPONSE_FORMAT);
        }

        logger.info("Finish processing PendingPayment [{}]", pendingPayment);
    }

    @Override
    public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) {
        logger.info("Start commiting PendingPayment [{}], with response {}", pendingPayment, getShortInfo(response));
        helper.failAttempt(pendingPayment, response.getDescriptionError());
        return null;
    }

    private String getShortInfo(PaymentSystemResponse response) {
        return "Response[successful=" + response.isSuccessful() + ", content=" +
               response.getMessage() != null ? response.getMessage() : response.getDescriptionError() + "]";
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return new PaymentSystemResponse() {
            @Override
            public String getDescriptionError() {
                return "iTunes payment was expired";
            }

            @Override
            public boolean isSuccessful() {
                return false;
            }
        };
    }

    private ITunesConnectionConfig getConnectionConfig(final String community) {
        return new ITunesConnectionConfig() {
            @Override
            public String getUrl() {
                return messageSource.getMessage(community, APPLE_IN_APP_I_TUNES_URL, null, null);
            }

            @Override
            public String getPassword() {
                return messageSource.getDecryptedMessage(community, APPLE_IN_APP_PASSWORD, null, null);
            }
        };
    }

    public void setiTunesClient(ITunesClient iTunesClient) {
        this.iTunesClient = iTunesClient;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setHelper(ITunesPaymentSystemServiceHelper helper) {
        this.helper = helper;
    }
}
