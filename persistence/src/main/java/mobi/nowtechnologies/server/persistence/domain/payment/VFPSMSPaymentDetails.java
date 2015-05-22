package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.VF_PSMS_TYPE)
public class VFPSMSPaymentDetails extends PSMSPaymentDetails {

    public VFPSMSPaymentDetails(PaymentPolicy paymentPolicy, User user, int retriesOnError) {
        setPaymentPolicy(paymentPolicy);
        setPhoneNumber(user.getMobile());
        setCreationTimestampMillis(Utils.getEpochMillis());
        setActivated(true);
        setLastPaymentStatus(NONE);
        setRetriesOnError(retriesOnError);
        resetMadeAttempts();
        setOwner(user);
    }

    public VFPSMSPaymentDetails() {
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.VF_PSMS_TYPE;
    }

    @Override
    public String toString() {
        return "VFPSMSPaymentDetails [" + super.toString() + "]";
    }

}
