package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.service.BasicResponse;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * User: gch
 * Date: 11/20/13
 */
public class PaymentTestUtils {

    public static BasicResponse createBasicResponse(final int statusCode,final String message){
        return new BasicResponse() {
            @Override
            public int getStatusCode() {
                return statusCode;
            }
            @Override public String getMessage() {
                return message;
            }
        };
    }

    public static PaymentPolicy createPaymentPolicy(){
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setAvailableInStore(true);
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
        paymentPolicy.setSubcost(BigDecimal.TEN);
        paymentPolicy.setSubweeks((byte)0);
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setTariff(Tariff._3G);
        return paymentPolicy;
    }
}
