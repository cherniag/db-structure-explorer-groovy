package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionInfo {
    private boolean ios;
    private boolean premium;
    private PaymentPolicyDto currentPaymentPolicy;
    private boolean freeTrial;
    private boolean isOnPaidPeriod;

    private List<PaymentPolicyDto> paymentPolicyDTOs = new ArrayList<>();

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

    public List<PaymentPolicyDto> getPaymentPolicyDTOs() {
        return paymentPolicyDTOs;
    }

    public void addPaymentPolicyDto(List<PaymentPolicyDto> paymentPolicyDTOs){
        this.paymentPolicyDTOs.addAll(paymentPolicyDTOs);
    }

    public PaymentPolicyDto getCurrentPaymentPolicy() {
        return currentPaymentPolicy;
    }

    public void setCurrentPaymentPolicy(PaymentPolicyDto currentPaymentPolicy) {
        this.currentPaymentPolicy = currentPaymentPolicy;
    }

    public boolean isFreeTrial() {
        return freeTrial;
    }

    public void setFreeTrial(boolean freeTrial) {
        this.freeTrial = freeTrial;
    }

    public boolean isOnPaidPeriod() {
        return isOnPaidPeriod;
    }

    public void setOnPaidPeriod(boolean isOnPaidPeriod) {
        this.isOnPaidPeriod = isOnPaidPeriod;
    }
}
