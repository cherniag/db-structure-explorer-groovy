package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.PeriodUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentPolicyFactory{

	public static PaymentPolicy createPaymentPolicy() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setSubcost(BigDecimal.ZERO);
		paymentPolicy.setPeriod(new Period().withDuration(5).withPeriodUnit(WEEKS));
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setShortCode("shortCode");
        paymentPolicy.setAvailableInStore(false);
        paymentPolicy.setPaymentType(O2_PSMS_TYPE);
		return paymentPolicy;
	}

     public static PaymentPolicy createPaymentPolicy(Tariff tariff) {
         PaymentPolicy paymentPolicy = createPaymentPolicy();
         paymentPolicy.setTariff(tariff);
         return paymentPolicy;
     }

    public static PaymentPolicy paymentPolicyWithDefaultNotNullFields() {
        return new PaymentPolicy().withPeriod(new Period().withDuration(1).withPeriodUnit(WEEKS)).withMediaType(AUDIO).withTariff(_3G).withSubCost(ONE).withDefault(true).withCommunity(new Community()).withOnline(true);
    }
}