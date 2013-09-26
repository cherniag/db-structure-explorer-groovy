package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue(PaymentDetails.MIG_SMS_TYPE)
public class MigPaymentDetails extends PaymentDetails {

	private String migPhoneNumber;
	
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