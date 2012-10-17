package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue(PaymentDetails.MIG_SMS_TYPE)
@NamedQueries({
	@NamedQuery(name=MigPaymentDetails.NQ_GET_PAYMENT_DETAILS_BY_PHONENUMBER, query="select mpd from MigPaymentDetails mpd where mpd.migPhoneNumber=?")
})
public class MigPaymentDetails extends PaymentDetails {
	
	public static final String NQ_GET_PAYMENT_DETAILS_BY_PHONENUMBER = "NQ_GET_PAYMENT_DETAILS_BY_PHONENUMBER";
	
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