package mobi.nowtechnologies.server.service.payment.request;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PayPalRequest extends AbstractPaymentRequest<PayPalRequest> {

    private final static String USER_NAME = "paypal.user";
    private final static String PASSWORD = "paypal.password";
    private final static String SIGNATURE = "paypal.signature";
    private final static String API_VERSION = "paypal.apiVersion";
    private final static String BTN_SOURCE = "paypal.btnSource";
    private final static String RETURN_URL = "paypal.returnURL";
    private final static String CANCEL_URL = "paypal.cancelURL";
    private final static String BILLING_TYPE = "paypal.lBillingType0";

    private DecimalFormat decimalFormat;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

	public PayPalRequest() {
	}

	protected PayPalRequest createDefaultRequest(String communityName) {
		return new PayPalRequest()
            .addParam(PayPalRequestParam.USER, communityResourceBundleMessageSource.getMessage(communityName, USER_NAME, null, null))
            .addParam(PayPalRequestParam.PWD, communityResourceBundleMessageSource.getMessage(communityName, PASSWORD, null, null))
            .addParam(PayPalRequestParam.VERSION, communityResourceBundleMessageSource.getMessage(communityName, API_VERSION, null, null))
            .addParam(PayPalRequestParam.BUTTONSOURCE, communityResourceBundleMessageSource.getMessage(communityName, BTN_SOURCE, null, null))
            .addParam(PayPalRequestParam.SIGNATURE, communityResourceBundleMessageSource.getMessage(communityName, SIGNATURE, null, null));
	}
	
	public PayPalRequest createTokenRequest(String billingAgreementDescription, String currencyCode) {
		return createTokenRequest(billingAgreementDescription, null, null, currencyCode, null);
	}
	
	public PayPalRequest createBillingAgreementRequest(String token, String communityName) {
		return createDefaultRequest(communityName)
			.addParam(PayPalRequestParam.TOKEN, token)
			.addParam(PayPalRequestParam.METHOD, "CreateBillingAgreement");
	}
	
	public PayPalRequest createReferenceTransactionRequest(String billingAgeementTxId, String currencyCode, BigDecimal amount, String communityName) {
		return createDefaultRequest(communityName)
			.addParam(PayPalRequestParam.AMT, decimalFormat.format(amount))
			.addParam(PayPalRequestParam.REFERENCEID, billingAgeementTxId)
			.addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
			.addParam(PayPalRequestParam.METHOD, "DoReferenceTransaction")
			.addParam(PayPalRequestParam.PAYMENTACTION, "Sale");
			
	}

    public PayPalRequest createTokenRequest(String billingAgreementDescription, String successUrl, String failUrl, String currencyCode, String communityName) {
        return createDefaultRequest(communityName)
            .addParam(PayPalRequestParam.RETURNURL, successUrl != null ? successUrl : communityResourceBundleMessageSource.getMessage(communityName, RETURN_URL, null, null))
            .addParam(PayPalRequestParam.CANCELURL, failUrl != null ? failUrl : communityResourceBundleMessageSource.getMessage(communityName, CANCEL_URL, null, null))
            .addParam(PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0, billingAgreementDescription)
            .addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
            .addParam(PayPalRequestParam.METHOD, "SetExpressCheckout")
            .addParam(PayPalRequestParam.L_BILLINGTYPE0, communityResourceBundleMessageSource.getMessage(communityName, BILLING_TYPE, null, null))
            .addParam(PayPalRequestParam.PAYMENTACTION, "Authorization");
    }

    public void setAmountFormat(String amountFormat) {
		decimalFormat = new DecimalFormat(amountFormat);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator('.');
			decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
	}

    @Required
    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }
}