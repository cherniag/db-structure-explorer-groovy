package mobi.nowtechnologies.server.service.itunes.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;

/**
 * Created by zam on 1/16/2015.
 */
public interface ITunesPaymentService {

    void createSubmittedPayment(User user, String appStoreReceipt, ITunesResult result);

    void checkForDuplicates(int userId, long nextSubPaymentTimestamp);

    boolean hasOneTimeSubscription(User user);

    PaymentPolicy getCurrentSubscribedPaymentPolicy(User user);

    long createXPlayCapPayment(User user, String receipt, ITunesResult response, int playCapValue);
}
