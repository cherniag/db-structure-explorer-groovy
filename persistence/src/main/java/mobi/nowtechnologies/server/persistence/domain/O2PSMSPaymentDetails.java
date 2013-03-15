package mobi.nowtechnologies.server.persistence.domain;

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
		return "O2PSMSPaymentDetails ["+ super.toString()+", phoneNumber=" + phoneNumber + "]";
	}

}
