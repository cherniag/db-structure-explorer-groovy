package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.service.BasicResponse;

import java.math.BigDecimal;

import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

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
        paymentPolicy.setPeriod(new Period().withDuration(1).withDurationUnit(WEEKS));
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setTariff(Tariff._3G);
        return paymentPolicy;
    }

    public static PaymentDetailsDto createPaymentDetailsDto() {
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        paymentDetailsDto.setAmount("2.50");
        paymentDetailsDto.setCurrency("EUR");
        paymentDetailsDto.setOfferId(1);
        paymentDetailsDto.setToken("78955453JH2KY00DTV1ZC8H");
        return paymentDetailsDto;
    }
}
