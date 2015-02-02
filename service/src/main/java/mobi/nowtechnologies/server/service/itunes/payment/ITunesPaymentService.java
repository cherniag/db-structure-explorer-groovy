package mobi.nowtechnologies.server.service.itunes.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;

/**
 * Created by zam on 1/16/2015.
 */
public interface ITunesPaymentService {
    void createSubmittedPayment(User user, String appStoreReceipt, ITunesResult result, ITunesPaymentService iTunesPaymentService);
    void checkForDuplicates(int userId, long nextSubPaymentTimestamp);
    boolean hasOneTimeSubscription(User user);
}
