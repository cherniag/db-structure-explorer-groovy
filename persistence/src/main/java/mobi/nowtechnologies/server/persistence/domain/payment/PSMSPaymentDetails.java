package mobi.nowtechnologies.server.persistence.domain.payment;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@DiscriminatorValue(PaymentDetails.PSMS_TYPE)
public class PSMSPaymentDetails extends PaymentDetails {

    @Column(name = "phone_number")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.PSMS_TYPE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("phoneNumber", phoneNumber).toString();
    }
}
