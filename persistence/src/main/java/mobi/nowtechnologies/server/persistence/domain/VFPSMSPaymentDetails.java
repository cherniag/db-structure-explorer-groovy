package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.VF_PSMS_TYPE)
public class VFPSMSPaymentDetails extends PaymentDetails {
	
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
		return PaymentDetails.VF_PSMS_TYPE;
	}

	@Override
	public String toString() {
		return "VFPSMSPaymentDetails ["+ super.toString()+", phoneNumber=" + phoneNumber + "]";
	}

}
