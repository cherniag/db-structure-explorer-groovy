package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.Date;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev Date: 4/14/2015
 */
@Entity
@DiscriminatorValue(PaymentDetails.ITUNES_SUBSCRIPTION)
public class ITunesPaymentDetails extends PaymentDetails {

    @Column(name = "app_strore_receipt")
    private String appStroreReceipt;

    protected ITunesPaymentDetails() {
    }

    public ITunesPaymentDetails(User user, String appStroreReceipt, int retriesOnError) {
        setOwner(user);
        updateAppStroreReceipt(appStroreReceipt);
        setRetriesOnError(retriesOnError);

        setActivated(true);
        setLastPaymentStatus(PaymentDetailsStatus.NONE);
        setCreationTimestampMillis(new Date().getTime());

        resetMadeAttempts();
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.ITUNES_SUBSCRIPTION;
    }

    public String getAppStroreReceipt() {
        return appStroreReceipt;
    }

    public void updateAppStroreReceipt(String appStroreReceipt) {
        Preconditions.checkArgument(appStroreReceipt != null && !appStroreReceipt.isEmpty());
        this.appStroreReceipt = appStroreReceipt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("appStroreReceipt", appStroreReceipt)
            .toString();
    }
}