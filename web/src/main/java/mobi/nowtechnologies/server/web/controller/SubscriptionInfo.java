package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;

public class SubscriptionInfo {
    private boolean ios;
    private boolean premium;
    private PaymentPolicyDto paymentPolicyDto;
    private String paymentPolicyMessage;

    public boolean isIos() {
        return ios;
    }

    public void setIos(boolean ios) {
        this.ios = ios;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public PaymentPolicyDto getPaymentPolicyDto() {
        return paymentPolicyDto;
    }

    public void setPaymentPolicyDto(PaymentPolicyDto paymentPolicyDto) {
        this.paymentPolicyDto = paymentPolicyDto;
    }

    public String getPaymentPolicyMessage() {
        return paymentPolicyMessage;
    }

    public void setPaymentPolicyMessage(String paymentPolicyMessage) {
        this.paymentPolicyMessage = paymentPolicyMessage;
    }
}
