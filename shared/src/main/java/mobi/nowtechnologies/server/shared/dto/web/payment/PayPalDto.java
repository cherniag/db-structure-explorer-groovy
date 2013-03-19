package mobi.nowtechnologies.server.shared.dto.web.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;

public class PayPalDto {
	
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

	public static PaymentDetailsDto toPaymentDetails(PayPalDto dto) {
		PaymentDetailsDto pdto = new PaymentDetailsDto();
			pdto.setPaymentType("PAY_PAL");
			pdto.setBillingAgreementDescription(dto.getBillingAgreementDescription());
			pdto.setSuccessUrl(dto.getSuccessUrl());
			pdto.setFailUrl(dto.getFailUrl());
			pdto.setPaymentPolicyId(dto.getPaymentPolicyId());
		return pdto;
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