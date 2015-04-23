package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.Date;

@Entity
@DiscriminatorValue(PaymentDetails.PAYPAL_TYPE)
public class PayPalPaymentDetails extends PaymentDetails {

    private String billingAgreementTxId;

    @Column(name = "token")
    private String token;

    @Column(name = "payerId")
    private String payerId;

    public PayPalPaymentDetails() {
    }

    public PayPalPaymentDetails(User user, PaymentPolicy paymentPolicy, String billingAgreement, String token, String payerId, int retriesOnError) {
        setBillingAgreementTxId(billingAgreement);
        setToken(token);
        setPayerId(payerId);
        setPaymentPolicy(paymentPolicy);
        setCreationTimestampMillis(new Date().getTime());
        setOwner(user);
        setActivated(true);
        setLastPaymentStatus(NONE);
        setRetriesOnError(retriesOnError);
        resetMadeAttempts();
    }

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