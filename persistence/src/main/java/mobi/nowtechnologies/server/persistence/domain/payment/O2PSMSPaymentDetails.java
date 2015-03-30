package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;

import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue(PaymentDetails.O2_PSMS_TYPE)
public class O2PSMSPaymentDetails extends PSMSPaymentDetails {

    public O2PSMSPaymentDetails(PaymentPolicy paymentPolicy, User user, int retriesOnError) {
        setPaymentPolicy(paymentPolicy);
        setPhoneNumber(user.getMobile());
        setCreationTimestampMillis(Utils.getEpochMillis());
        setActivated(true);
        setLastPaymentStatus(NONE);
        setRetriesOnError(retriesOnError);
        resetMadeAttempts();
        setOwner(user);
    }

    public O2PSMSPaymentDetails() {
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.O2_PSMS_TYPE;
    }

    @Override
    public String toString() {
        return "O2PSMSPaymentDetails [" + super.toString() + "]";
    }

}
