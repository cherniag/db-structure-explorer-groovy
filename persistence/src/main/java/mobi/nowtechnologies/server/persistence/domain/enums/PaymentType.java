package mobi.nowtechnologies.server.persistence.domain.enums;

public enum PaymentType {
    SAGEPAY_CREDITCARD("sagePayCreditCard"),
    PAYPAL("payPal"),
    MIG_SMS("migSms"),
    O2_PSMS("o2Psms"),
    ITUNES_SUBSCRIPTION("iTunesSubscription"),
    UNKNOWN("unknown");

    private String type;

    private PaymentType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
