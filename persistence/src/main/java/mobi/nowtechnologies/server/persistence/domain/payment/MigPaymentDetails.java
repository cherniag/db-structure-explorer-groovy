package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.Date;

@Entity
@DiscriminatorValue(PaymentDetails.MIG_SMS_TYPE)
public class MigPaymentDetails extends PaymentDetails {

    private String migPhoneNumber;

    public MigPaymentDetails() {
    }

    public MigPaymentDetails(String migPhoneNumber, User user, PaymentPolicy paymentPolicy, int retriesOnError) {
        setCreationTimestampMillis(new Date().getTime());
        setLastPaymentStatus(PaymentDetailsStatus.PENDING);
        resetMadeAttempts();
        setRetriesOnError(retriesOnError);
        setMigPhoneNumber(migPhoneNumber);
        setPaymentPolicy(paymentPolicy);
        setActivated(false);
        setOwner(user);
    }

    public String getMigPhoneNumber() {
        return migPhoneNumber;
    }

    public void setMigPhoneNumber(String migPhoneNumber) {
        this.migPhoneNumber = migPhoneNumber;
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.MIG_SMS_TYPE;
    }
}