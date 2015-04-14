package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.PAYPAL_TYPE)
public class PayPalPaymentDetails extends PaymentDetails {

    private String billingAgreementTxId;

    @Column(name = "token")
    private String token;

    @Column(name = "payerId")
    private String payerId;

    public String getBillingAgreementTxId() {
        return billingAgreementTxId;
    }

    public void setBillingAgreementTxId(String billingAgreementTxId) {
        this.billingAgreementTxId = billingAgreementTxId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    @Override
    public String getPaymentType() {
        return PaymentDetails.PAYPAL_TYPE;
    }
}