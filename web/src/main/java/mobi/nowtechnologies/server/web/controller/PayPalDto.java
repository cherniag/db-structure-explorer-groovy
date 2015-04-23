package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.payment.PayPalPaymentDetailsService;

public class PayPalDto implements PayPalPaymentDetailsService.PayPalDetailsInfo {

    public static final String NAME = "payPalDto";

    private Integer paymentPolicyId;

    private String billingAgreementDescription;

    private String successUrl;

    private String failUrl;

    public PayPalDto() {
    }

    public String getBillingAgreementDescription() {
        return billingAgreementDescription;
    }

    public void setBillingAgreementDescription(String billingAgreementDescription) {
        this.billingAgreementDescription = billingAgreementDescription;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }

    public Integer getPaymentPolicyId() {
        return paymentPolicyId;
    }

    public void setPaymentPolicyId(Integer paymentPoliceId) {
        this.paymentPolicyId = paymentPoliceId;
    }
}