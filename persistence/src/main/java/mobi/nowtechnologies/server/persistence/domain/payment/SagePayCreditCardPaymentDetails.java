package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.SAGEPAY_CREDITCARD_TYPE)
public class SagePayCreditCardPaymentDetails extends PaymentDetails {
	
	private String vendorTxCode;
	private String VPSTxId;
	private String securityKey;
	private String txAuthNo;
	private Boolean released;
	
	public String getVendorTxCode() {
		return vendorTxCode;
	}
	public void setVendorTxCode(String vendorTxCode) {
		this.vendorTxCode = vendorTxCode;
	}
	public String getVPSTxId() {
		return VPSTxId;
	}
	public void setVPSTxId(String vPSTxId) {
		VPSTxId = vPSTxId;
	}
	public String getSecurityKey() {
		return securityKey;
	}
	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}
	public String getTxAuthNo() {
		return txAuthNo;
	}
	public void setTxAuthNo(String txAuthNo) {
		this.txAuthNo = txAuthNo;
	}
	public Boolean getReleased() {
		return released;
	}
	public void setReleased(Boolean released) {
		this.released = released;
	}
	
	@Override
	public String getPaymentType() {
		return PaymentDetails.SAGEPAY_CREDITCARD_TYPE;
	}	
}