package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/5/2015
 */
@Entity
@DiscriminatorValue(PaymentDetails.MTVNZ_PSMS_TYPE)
public class MTVNZPSMSPaymentDetails extends PSMSPaymentDetails {

    public MTVNZPSMSPaymentDetails(PaymentPolicy paymentPolicy, User user, int retriesOnError) {
        setPaymentPolicy(paymentPolicy);
        setPhoneNumber(user.getMobile());
        setCreationTimestampMillis(Utils.getEpochMillis());
        setActivated(true);
        setLastPaymentStatus(NONE);
        setRetriesOnError(retriesOnError);
        resetMadeAttempts();
        setOwner(user);
    }

    public MTVNZPSMSPaymentDetails() {
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.MTVNZ_PSMS_TYPE;
    }

}
