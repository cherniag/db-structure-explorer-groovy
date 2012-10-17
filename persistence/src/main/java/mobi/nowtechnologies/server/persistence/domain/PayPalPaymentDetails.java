package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.PAYPAL_TYPE)
public class PayPalPaymentDetails extends PaymentDetails {
	
	private String billingAgreementTxId;

	public String getBillingAgreementTxId() {
		return billingAgreementTxId;
	}

	public void setBillingAgreementTxId(String billingAgreementTxId) {
		this.billingAgreementTxId = billingAgreementTxId;
	}

	@Override
	public String getPaymentType() {
		return PaymentDetails.PAYPAL_TYPE;
	}
}