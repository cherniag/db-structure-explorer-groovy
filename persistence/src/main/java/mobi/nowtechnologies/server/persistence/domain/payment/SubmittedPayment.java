package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "tb_submittedPayments")
@NamedQuery(name = SubmittedPayment.NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC,
            query = "select submittedPayment from SubmittedPayment submittedPayment where submittedPayment.userId=? and submittedPayment.status='SUCCESSFUL' order by submittedPayment.timestamp desc")
public class SubmittedPayment extends AbstractPayment {

    public static final String NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC = "NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC";

    private String descriptionError;

    @Enumerated(EnumType.STRING)
    private PaymentDetailsStatus status;

    @Lob
    @Column(name = "base64_encoded_app_store_receipt")
    private String base64EncodedAppStoreReceipt;

    @Column(name = "app_store_original_transaction_id")
    private String appStoreOriginalTransactionId;

    @Column(name = "next_sub_payment")
    private int nextSubPayment;

    @ManyToOne
    @JoinColumn(name = "payment_policy_id")
    private PaymentPolicy paymentPolicy;

    public static SubmittedPayment valueOf(PendingPayment pendingPayment) {
        SubmittedPayment payment = new SubmittedPayment();
        BeanUtils.copyProperties(pendingPayment, payment);
        payment.setI(null);
        if (payment.getExternalTxId() == null) {
            payment.setExternalTxId("");
        }
        return payment;
    }

    @Override
    public void setPaymentDetails(PaymentDetails paymentDetails) {
        if (paymentDetails != null) {
            setPaymentPolicy(paymentDetails.getPaymentPolicy());
        }
        super.setPaymentDetails(paymentDetails);
    }

    public PaymentPolicy getPaymentPolicy() {
        return paymentPolicy;
    }

    public void setPaymentPolicy(PaymentPolicy paymentPolicy) {
        this.paymentPolicy = paymentPolicy;
    }

    public String getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(String descriptionError) {
        this.descriptionError = descriptionError;
    }

    public PaymentDetailsStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentDetailsStatus status) {
        this.status = status;
    }

    public String getBase64EncodedAppStoreReceipt() {
        return base64EncodedAppStoreReceipt;
    }

    public void setBase64EncodedAppStoreReceipt(String base64EncodedAppStoreReceipt) {
        this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
    }

    public String getAppStoreOriginalTransactionId() {
        return appStoreOriginalTransactionId;
    }

    public void setAppStoreOriginalTransactionId(String appStoreOriginalTransactionId) {
        this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
    }

    public int getNextSubPayment() {
        return nextSubPayment;
    }

    public void setNextSubPayment(int nextSubPayment) {
        this.nextSubPayment = nextSubPayment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("base64EncodedAppStoreReceipt", base64EncodedAppStoreReceipt)
                                        .append("appStoreOriginalTransactionId", appStoreOriginalTransactionId).append("descriptionError", descriptionError).append("nextSubPayment", nextSubPayment)
                                        .append("status", status).append("paymentPolicy", paymentPolicy).toString();
    }
}