package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;

public class PaymentCallback {

    private PaymentStatus statusCode;
    private String description;
    private String internalTxId;

    public PaymentCallback() {
    }

    public PaymentCallback(PaymentStatus statusCode, String description, String internalTxId) {
        super();
        this.statusCode = statusCode;
        this.description = description;
        this.internalTxId = internalTxId;
    }

    public PaymentStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(PaymentStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternalTxId() {
        return internalTxId;
    }

    public void setInternalTxId(String internalTxId) {
        this.internalTxId = internalTxId;
    }
}