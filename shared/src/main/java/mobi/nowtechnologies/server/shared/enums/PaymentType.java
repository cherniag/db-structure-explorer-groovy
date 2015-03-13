package mobi.nowtechnologies.server.shared.enums;

public enum PaymentType {

    SAGEPAY_CREDITCARD("sagePayCreditCard"),
    PAYPAL("payPal"),
    MIG_SMS("migSms"),
    O2_PSMS("o2Psms"),
    ITUNES_SUBSCRIPTION("iTunesSubscription"),
    UNKNOWN("unknown");

    private String type;

    private PaymentType(String type) {
        this.type = type;
    }

    public static PaymentType valueOfByType(String type) {
        PaymentType[] paymentTypes = values();
        for (int s = 0; s < paymentTypes.length; s++) {
            if (paymentTypes[s].equals(type)) {
                return paymentTypes[s];
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return type;
    }
}
