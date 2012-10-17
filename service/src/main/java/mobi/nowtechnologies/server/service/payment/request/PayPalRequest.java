package mobi.nowtechnologies.server.service.payment.request;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PayPalRequest extends AbstractPaymentRequest<PayPalRequest> {

	public static enum PayPalRequestParam implements PaymentRequestParam {
		L_BILLINGAGREEMENTDESCRIPTION0,
		L_BILLINGTYPE0,
		PAYMENTACTION,
		CURRENCYCODE,
		RETURNURL,
		CANCELURL,
		TOKEN,
		METHOD,
		VERSION,
		USER,
		PWD,
		BUTTONSOURCE,
		SIGNATURE,
		REFERENCEID,
		AMT
	}
	
	private String user;
	private String password;
	private String signature;
	private String apiVersion;
	private String btnSource;
	private String returnURL;
	private String cancelURL;
	private DecimalFormat decimalFormat;
	
	public PayPalRequest() {
	}
	
	protected PayPalRequest createDefaultRequest() {
		return new PayPalRequest()
		.addParam(PayPalRequestParam.USER, user)
		.addParam(PayPalRequestParam.PWD, password)
		.addParam(PayPalRequestParam.VERSION, apiVersion)
		.addParam(PayPalRequestParam.BUTTONSOURCE, btnSource)
		.addParam(PayPalRequestParam.SIGNATURE, signature);
	}
	
	public PayPalRequest createTokenRequest(String billingAgreementDescription, String currencyCode) {
		return createTokenRequest(billingAgreementDescription, null, null, currencyCode);
	}
	
	public PayPalRequest createBillingAgreementRequest(String token) {
		return createDefaultRequest()
			.addParam(PayPalRequestParam.TOKEN, token)
			.addParam(PayPalRequestParam.METHOD, "CreateBillingAgreement");
	}
	
	public PayPalRequest createReferenceTransactionRequest(String billingAgeementTxId, String currencyCode, BigDecimal amount) {
		return createDefaultRequest()
			.addParam(PayPalRequestParam.AMT, decimalFormat.format(amount))
			.addParam(PayPalRequestParam.REFERENCEID, billingAgeementTxId)
			.addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
			.addParam(PayPalRequestParam.METHOD, "DoReferenceTransaction")
			.addParam(PayPalRequestParam.PAYMENTACTION, "Sale");
			
	}
	
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public void setBtnSource(String btnSource) {
		this.btnSource = btnSource;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public void setCancelURL(String cancelURL) {
		this.cancelURL = cancelURL;
	}

	public void setAmountFormat(String amountFormat) {
		decimalFormat = new DecimalFormat(amountFormat);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator('.');
			decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
	}

	public PayPalRequest createTokenRequest(String billingAgreementDescription, String successUrl, String failUrl, String currencyCode) {
		return createDefaultRequest()
				.addParam(PayPalRequestParam.RETURNURL, successUrl != null ? successUrl : returnURL)
				.addParam(PayPalRequestParam.CANCELURL, failUrl != null ? failUrl : cancelURL)
				.addParam(PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0, billingAgreementDescription)
				.addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
				.addParam(PayPalRequestParam.METHOD, "SetExpressCheckout")
				.addParam(PayPalRequestParam.L_BILLINGTYPE0, "MerchantInitiatedBillingSingleAgreement")
				.addParam(PayPalRequestParam.PAYMENTACTION, "Authorization");
	}
}