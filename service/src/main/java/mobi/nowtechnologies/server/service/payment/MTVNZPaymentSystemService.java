package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.NZProviderType;
import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoProvider;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.ProviderConnectionException;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Author: Gennadii Cherniaiev Date: 4/1/2015
 */
public class MTVNZPaymentSystemService implements PaymentSystemService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private NZSubscriberInfoProvider nzSubscriberInfoProvider;
    private MTVNZPaymentHelper mtvnzPaymentHelper;

    @Override
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        logger.info("Start payment: {}", pendingPayment);
        final PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final String normalizePhoneNumber = normalizePhoneNumber(paymentDetails.getPhoneNumber());

        try {
            NZSubscriberResult subscriberResult = nzSubscriberInfoProvider.getSubscriberResult(normalizePhoneNumber);
            NZProviderType nzProviderType = NZProviderType.of(subscriberResult.getProviderName());
            if(nzProviderType != NZProviderType.VODAFONE){
                logger.info("User {} is not VF subscriber", normalizePhoneNumber);
                mtvnzPaymentHelper.finishPaymentForNotVFUser(pendingPayment, "User does not belong to VF");
                return;
            }
            mtvnzPaymentHelper.startPayment(pendingPayment);
        } catch (ProviderConnectionException e) {
            logger.warn("Connection problem to VF subscriber service: {}", e.getMessage());
            mtvnzPaymentHelper.skipAttemptWithoutRetryIncrement(pendingPayment, e.getMessage());
        } catch (MsisdnNotFoundException e) {
            logger.warn("User {} with phone number {} was not found in VF", pendingPayment.getUserId(), normalizePhoneNumber);
            mtvnzPaymentHelper.finishPaymentForNotVFUser(pendingPayment, "MSISDN not found");
        }

    }

    @Override
    public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) {
        return mtvnzPaymentHelper.commitPayment(pendingPayment, response);
    }

    @Override
    public int getRetriesOnError() {
        return mtvnzPaymentHelper.getRetriesOnError();
    }

    @Override
    public long getExpireMillis() {
        return mtvnzPaymentHelper.getExpireMillis();
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return mtvnzPaymentHelper.getExpiredResponse();
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if(phoneNumber.startsWith("+")) {
            return phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    public void setNzSubscriberInfoProvider(NZSubscriberInfoProvider nzSubscriberInfoProvider) {
        this.nzSubscriberInfoProvider = nzSubscriberInfoProvider;
    }

    public void setMtvnzPaymentHelper(MTVNZPaymentHelper mtvnzPaymentHelper) {
        this.mtvnzPaymentHelper = mtvnzPaymentHelper;
    }
}
