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
    private static final String METHOD_CREATE_BILLING_AGREEMENT = "CreateBillingAgreement";
    private static final String METHOD_DO_REFERENCE_TRANSACTION = "DoReferenceTransaction";
    private static final String METHOD_SET_EXPRESS_CHECKOUT = "SetExpressCheckout";
    private static final String ACTION_SALE = "Sale";
    private static final String ACTION_AUTHORIZATION = "Authorization";

    private DecimalFormat decimalFormat;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

	public PayPalRequest() {
	}

	protected PayPalRequest createDefaultRequest(String communityRewriteUrlParameter) {
		return new PayPalRequest()
            .addParam(PayPalRequestParam.USER, getValueOf(communityRewriteUrlParameter, USER_NAME))
            .addParam(PayPalRequestParam.PWD, getValueOf(communityRewriteUrlParameter, PASSWORD))
            .addParam(PayPalRequestParam.VERSION, getValueOf(communityRewriteUrlParameter, API_VERSION))
            .addParam(PayPalRequestParam.BUTTONSOURCE, getValueOf(communityRewriteUrlParameter, BTN_SOURCE))
            .addParam(PayPalRequestParam.SIGNATURE, getValueOf(communityRewriteUrlParameter, SIGNATURE));
	}

    private String getValueOf(String communityRewriteUrlParameter, String code) {
        return communityResourceBundleMessageSource.getDecryptedMessage(communityRewriteUrlParameter, code, null, null);
    }

    public PayPalRequest createBillingAgreementRequest(String token, String communityRewriteUrlParameter) {
		return createDefaultRequest(communityRewriteUrlParameter)
			.addParam(PayPalRequestParam.TOKEN, token)
			.addParam(PayPalRequestParam.METHOD, METHOD_CREATE_BILLING_AGREEMENT);
	}
	
	public PayPalRequest createReferenceTransactionRequest(String billingAgreementTxId, String currencyCode, BigDecimal amount, String communityRewriteUrlParameter) {
		return createDefaultRequest(communityRewriteUrlParameter)
			.addParam(PayPalRequestParam.AMT, decimalFormat.format(amount))
			.addParam(PayPalRequestParam.REFERENCEID, billingAgreementTxId)
			.addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
			.addParam(PayPalRequestParam.METHOD, METHOD_DO_REFERENCE_TRANSACTION)
			.addParam(PayPalRequestParam.PAYMENTACTION, ACTION_SALE);
			
	}

    public PayPalRequest createTokenRequest(String billingAgreementDescription, String successUrl, String failUrl, String currencyCode, String communityRewriteUrlParameter) {
        return createDefaultRequest(communityRewriteUrlParameter)
            .addParam(PayPalRequestParam.RETURNURL, successUrl != null ? successUrl : getValueOf(communityRewriteUrlParameter, RETURN_URL))
            .addParam(PayPalRequestParam.CANCELURL, failUrl != null ? failUrl : getValueOf(communityRewriteUrlParameter, CANCEL_URL))
            .addParam(PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0, billingAgreementDescription)
            .addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
            .addParam(PayPalRequestParam.METHOD, METHOD_SET_EXPRESS_CHECKOUT)
            .addParam(PayPalRequestParam.L_BILLINGTYPE0, getValueOf(communityRewriteUrlParameter, BILLING_TYPE))
            .addParam(PayPalRequestParam.PAYMENTACTION, ACTION_AUTHORIZATION);
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