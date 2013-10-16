package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.PSMS_TYPE)
public class PSMSPaymentDetails extends PaymentDetails {
	
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
		return PaymentDetails.PSMS_TYPE;
	}

	@Override
	public String toString() {
		return "PSMSPaymentDetails ["+ super.toString()+", phoneNumber=" + phoneNumber + "]";
	}

}
