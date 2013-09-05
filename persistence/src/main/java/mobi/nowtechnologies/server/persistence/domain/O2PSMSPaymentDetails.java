package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Entity
@DiscriminatorValue(PaymentDetails.O2_PSMS_TYPE)
public class O2PSMSPaymentDetails extends PaymentDetails {
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String getPaymentType() {
		return PaymentDetails.O2_PSMS_TYPE;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append("phoneNumber", phoneNumber)
                .toString();
    }
}
