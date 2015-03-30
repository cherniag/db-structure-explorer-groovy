package mobi.nowtechnologies.server.service.payment.request;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.springframework.beans.factory.annotation.Required;

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
    private static final String METHOD_GET_EXPRESS_CHECKOUT_DETAILS = "GetExpressCheckoutDetails";
    private static final String METHOD_DO_EXPRESS_CHECKOUT_PAYMENT = "DoExpressCheckoutPayment";
    private static final String ACTION_SALE = "Sale";
    private static final String ACTION_AUTHORIZATION = "Authorization";
    private static final String SOLUTION_TYPE_SOLE = "Sole";

    private DecimalFormat decimalFormat;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    public PayPalRequest() {
    }

    public PayPalRequest createBillingAgreementRequest(String token, String communityRewriteUrlParameter) {
        return createDefaultRequest(communityRewriteUrlParameter).addParam(PayPalRequestParam.TOKEN, token).addParam(PayPalRequestParam.METHOD, METHOD_CREATE_BILLING_AGREEMENT);
    }

    public PayPalRequest createCheckoutDetailsRequest(String token, String communityRewriteUrlParameter) {
        return createDefaultRequest(communityRewriteUrlParameter).addParam(PayPalRequestParam.TOKEN, token).addParam(PayPalRequestParam.METHOD, METHOD_GET_EXPRESS_CHECKOUT_DETAILS);
    }

    public PayPalRequest createReferenceTransactionRequest(String billingAgreementTxId, String currencyCode, BigDecimal amount, String communityRewriteUrlParameter) {
        return createDefaultRequest(communityRewriteUrlParameter).addParam(PayPalRequestParam.AMT, decimalFormat.format(amount)).addParam(PayPalRequestParam.REFERENCEID, billingAgreementTxId)
                                                                 .addParam(PayPalRequestParam.CURRENCYCODE, currencyCode).addParam(PayPalRequestParam.METHOD, METHOD_DO_REFERENCE_TRANSACTION)
                                                                 .addParam(PayPalRequestParam.PAYMENTACTION, ACTION_SALE);
    }

    public PayPalRequest createDoPaymentRequest(String token, String payerId, String currencyCode, BigDecimal amount, String communityRewriteUrlParameter) {
        return createDefaultRequest(communityRewriteUrlParameter).addParam(PayPalRequestParam.PAYMENTREQUEST_0_AMT, decimalFormat.format(amount))
                                                                 .addParam(PayPalRequestParam.TOKEN, token)
                                                                 .addParam(PayPalRequestParam.PAYERID, payerId)
                                                                 .addParam(PayPalRequestParam.PAYMENTREQUEST_0_CURRENCYCODE, currencyCode)
                                                                 .addParam(PayPalRequestParam.METHOD, METHOD_DO_EXPRESS_CHECKOUT_PAYMENT)
                                                                 .addParam(PayPalRequestParam.PAYMENTREQUEST_0_PAYMENTACTION, ACTION_SALE);
    }

    public PayPalRequest createBillingAgreementTokenRequest(String successUrl, String failUrl, String currencyCode, String community, String billingAgreementDescription) {
        return createDefaultRequest(community).addDefaultParamsForToken(resolveUrl(successUrl, community, RETURN_URL), resolveUrl(failUrl, community, CANCEL_URL), currencyCode)
                                              .addParam(PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0, billingAgreementDescription)
                                              .addParam(PayPalRequestParam.L_BILLINGTYPE0, getValueOf(community, BILLING_TYPE));
    }

    public PayPalRequest createOnetimePaymentTokenRequest(String successUrl, String failUrl, String currencyCode, String community, BigDecimal amount) {
        return createDefaultRequest(community).addDefaultParamsForToken(resolveUrl(successUrl, community, RETURN_URL), resolveUrl(failUrl, community, CANCEL_URL), currencyCode)
                                              .addParam(PayPalRequestParam.SOLUTIONTYPE, SOLUTION_TYPE_SOLE)
                                              .addParam(PayPalRequestParam.AMT, decimalFormat.format(amount));
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

    //
    // Internal stuff
    //
    private String resolveUrl(String url, String community, String defaultKey) {
        return url != null ? url : getValueOf(community, defaultKey);
    }

    protected PayPalRequest createDefaultRequest(String communityRewriteUrlParameter) {
        return new PayPalRequest().addParam(PayPalRequestParam.USER, getValueOf(communityRewriteUrlParameter, USER_NAME))
                                  .addParam(PayPalRequestParam.PWD, getValueOf(communityRewriteUrlParameter, PASSWORD))
                                  .addParam(PayPalRequestParam.VERSION, getValueOf(communityRewriteUrlParameter, API_VERSION))
                                  .addParam(PayPalRequestParam.BUTTONSOURCE, getValueOf(communityRewriteUrlParameter, BTN_SOURCE))
                                  .addParam(PayPalRequestParam.SIGNATURE, getValueOf(communityRewriteUrlParameter, SIGNATURE));
    }

    private PayPalRequest addDefaultParamsForToken(String successUrl, String failUrl, String currencyCode) {
        return this.addParam(PayPalRequestParam.RETURNURL, successUrl)
                   .addParam(PayPalRequestParam.CANCELURL, failUrl)
                   .addParam(PayPalRequestParam.METHOD, METHOD_SET_EXPRESS_CHECKOUT)
                   .addParam(PayPalRequestParam.CURRENCYCODE, currencyCode)
                   .addParam(PayPalRequestParam.PAYMENTACTION, ACTION_AUTHORIZATION);
    }

    private String getValueOf(String communityRewriteUrl, String code) {
        return communityResourceBundleMessageSource.getDecryptedMessage(communityRewriteUrl, code, null, null);
    }
}