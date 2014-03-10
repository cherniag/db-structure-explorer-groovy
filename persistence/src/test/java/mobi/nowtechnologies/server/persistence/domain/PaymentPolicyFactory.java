package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentPolicyFactory{

	public static PaymentPolicy createPaymentPolicy() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setSubcost(BigDecimal.ZERO);
		paymentPolicy.setSubweeks((byte)5);
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
        return new PaymentPolicy().withMediaType(AUDIO).withTariff(_3G).withSubCost(ONE).withDefault(true).withCommunity(CommunityDao.getCommunity("o2")).withOnline(true);
    }
}