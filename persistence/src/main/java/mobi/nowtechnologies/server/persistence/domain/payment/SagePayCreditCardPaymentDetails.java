package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;

import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.SAGEPAY_CREDITCARD_TYPE)
public class SagePayCreditCardPaymentDetails extends PaymentDetails {
    public static interface DetailsInfo {
        String getTxAuthNo();
        String getVPSTxId();
        String getSecurityKey();
    }

    private String vendorTxCode;
    private String VPSTxId;
    private String securityKey;
    private String txAuthNo;
    private Boolean released;

    public SagePayCreditCardPaymentDetails() {
    }

    public SagePayCreditCardPaymentDetails(DetailsInfo info, User user, PaymentPolicy paymentPolicy, int retriesOnError, String vendorTxCode) {
        setReleased(false);
        setSecurityKey(info.getSecurityKey());
        setTxAuthNo(info.getTxAuthNo());
        setVPSTxId(info.getVPSTxId());
        setVendorTxCode(vendorTxCode);
        setCreationTimestampMillis(Utils.getEpochMillis());
        setPaymentPolicy(paymentPolicy);
        setOwner(user);
        setActivated(true);
        setLastPaymentStatus(NONE);
        setRetriesOnError(retriesOnError);
        resetMadeAttempts();
    }

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