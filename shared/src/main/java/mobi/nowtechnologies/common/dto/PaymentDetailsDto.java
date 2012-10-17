package mobi.nowtechnologies.common.dto;



public class PaymentDetailsDto {
	
	private String paymentType;
	private String amount;
	private String currency;
	private String description;
	private int offerId;
	
	// SagePay options
	private String txType;
	private String cardHolderFirstName;
	private String cardHolderLastName;
	private String cardNumber;
	private String cardStartDate;
	private String cardExpirationDate;
	private String cardIssueNumber;
	private String cardCv2;
	private String cardType;
	private String billingAddress1;
	private String billingPostCode;
	private String billingCity;
	private String billingCountry;
	private String vendorTxCode;
	private int operator;
	
	// PayPal options
	private String token;
	private String billingAgreementDescription;
	private String successUrl;
	private String failUrl;
	
	// MIG options
	private String phoneNumber;
	
	
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	public String getCardHolderFirstName() {
		return cardHolderFirstName;
	}
	public void setCardHolderFirstName(String cardHolderFirstName) {
		this.cardHolderFirstName = cardHolderFirstName;
	}
	public String getCardHolderLastName() {
		return cardHolderLastName;
	}
	public void setCardHolderLastName(String cardHolderLastName) {
		this.cardHolderLastName = cardHolderLastName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardStartDate() {
		return cardStartDate;
	}
	public void setCardStartDate(String cardStartDate) {
		this.cardStartDate = cardStartDate;
	}
	public String getCardExpirationDate() {
		return cardExpirationDate;
	}
	public void setCardExpirationDate(String cardExpirationDate) {
		this.cardExpirationDate = cardExpirationDate;
	}
	public String getCardIssueNumber() {
		return cardIssueNumber;
	}
	public void setCardIssueNumber(String cardIssueNumber) {
		this.cardIssueNumber = cardIssueNumber;
	}
	public String getCardCv2() {
		return cardCv2;
	}
	public void setCardCv2(String cardCv2) {
		this.cardCv2 = cardCv2;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getBillingAddress1() {
		return billingAddress1;
	}
	public void setBillingAddress(String billingAddress1) {
		this.billingAddress1 = billingAddress1;
	}
	public String getBillingPostCode() {
		return billingPostCode;
	}
	public void setBillingPostCode(String billingPostCode) {
		this.billingPostCode = billingPostCode;
	}
	public String getBillingCity() {
		return billingCity;
	}
	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}
	public String getBillingCountry() {
		return billingCountry;
	}
	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}
	public String getVendorTxCode() {
		return vendorTxCode;
	}
	public void setVendorTxCode(String vendorTxCode) {
		this.vendorTxCode = vendorTxCode;
	}
	public String getBillingAgreementDescription() {
		return billingAgreementDescription;
	}
	public void setBillingAgreementDescription(String billingAgreementDescription) {
		this.billingAgreementDescription = billingAgreementDescription;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getOfferId() {
		return offerId;
	}
	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}
}