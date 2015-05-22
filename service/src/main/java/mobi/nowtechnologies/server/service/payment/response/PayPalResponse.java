package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayPalResponse extends PaymentSystemResponse {

    public static final String ACK_SUCCESS = "Success";
    public static final String ACK_SUCCESSFUL_WITH_WARNINGS = "SuccessWithWarning";
    public static final String L_ERRORCODE_PREFIX = "L_ERRORCODE";
    public static final String L_SHORTMESSAGE_PREFIX = "L_SHORTMESSAGE0";
    public static final String L_LONGMESSAGE_PREFIX = "L_LONGMESSAGE";
    public static final String L_SEVERITYCODE = "L_SEVERITYCODE";
    private static final Logger LOGGER = LoggerFactory.getLogger(PayPalResponse.class);
    private Map<PayPalResponseParam, String> properties;
    private Map<String, String> errors;

    public PayPalResponse(BasicResponse response) {
        super(response, false);
        properties = new HashMap<PayPalResponseParam, String>();
        errors = new HashMap<String, String>();
        if (HttpServletResponse.SC_OK == response.getStatusCode()) {
            try {
                String[] tuples = URLDecoder.decode(response.getMessage(), "UTF-8").split("&");
                for (int i = 0; i < tuples.length; i++) {
                    String[] paramTuple = tuples[i].split("=", 2);
                    try {
                        PayPalResponseParam key = PayPalResponseParam.valueOf(paramTuple[0]);
                        String value = paramTuple[1];
                        properties.put(key, value);
                    } catch (IllegalArgumentException e) {
                        if (paramTuple.length > 1) {
                            errors.put(paramTuple[0], paramTuple[1]);
                        } else {
                            descriptionError = paramTuple[0];
                        }
                    }
                }
                if (ACK_SUCCESS.equals(getAck()) || ACK_SUCCESSFUL_WITH_WARNINGS.equals(getAck())) {
                    isSuccessful = true;
                } else if (hasErrors(response.getMessage())) {
                    isSuccessful = false;
                    descriptionError = errors.values().toString();
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("PayPal responsed with bad encoding format");
            }
        } else {
            descriptionError = message;
            LOGGER.error("Http error from PayPal");
        }
    }

    private boolean hasErrors(String message) {
        return ((message.indexOf(L_ERRORCODE_PREFIX) != -1) || (message.indexOf(L_SHORTMESSAGE_PREFIX) != -1) || (message.indexOf(L_LONGMESSAGE_PREFIX) != -1) ||
                (message.indexOf(L_SEVERITYCODE) != -1));
    }

    public String getAck() {
        return properties.get(PayPalResponseParam.ACK);
    }

    public String getToken() {
        return properties.get(PayPalResponseParam.TOKEN);
    }

    public String getBillingAgreement() {
        return properties.get(PayPalResponseParam.BILLINGAGREEMENTID);
    }

    public String getCorrelationId() {
        return properties.get(PayPalResponseParam.CORRELATIONID);
    }

    public String getTransactionId() {
        String legacyTransactionIdCouldBeNullForNewTypePayments = properties.get(PayPalResponseParam.TRANSACTIONID);
        String newPaymentTypeTransactionId = properties.get(PayPalResponseParam.PAYMENTINFO_0_TRANSACTIONID);

        return legacyTransactionIdCouldBeNullForNewTypePayments != null ? legacyTransactionIdCouldBeNullForNewTypePayments : newPaymentTypeTransactionId;
    }

    public String getPayerId() {
        return properties.get(PayPalResponseParam.PAYERID);
    }

    public static enum PayPalResponseParam {
        TOKEN,
        TIMESTAMP,
        CORRELATIONID,
        ACK,
        VERSION,
        BUILD,
        BILLINGAGREEMENTID,
        TRANSACTIONID,
        PAYERID,
        PAYMENTINFO_0_TRANSACTIONID
    }

}