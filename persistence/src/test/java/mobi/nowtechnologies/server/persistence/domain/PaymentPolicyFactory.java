package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

import java.math.BigDecimal;
import java.util.Date;
import static java.math.BigDecimal.ONE;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentPolicyFactory {

    public static PaymentPolicy createPaymentPolicy() {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setSubcost(BigDecimal.ZERO);
        paymentPolicy.setPeriod(new Period().withDuration(5).withDurationUnit(WEEKS));
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

    public static PaymentPolicy createPaymentPolicy(PaymentPolicyType paymentPolicyType, String currencyISO, BigDecimal amount, String communityName) {
        PaymentPolicy paymentPolicy = createPaymentPolicy(paymentPolicyType, communityName);
        when(paymentPolicy.getCurrencyISO()).thenReturn(currencyISO);
        when(paymentPolicy.getSubcost()).thenReturn(amount);
        return paymentPolicy;
    }

    public static PaymentPolicy createPaymentPolicy(PaymentPolicyType paymentPolicyType, String communityName) {
        PaymentPolicy paymentPolicy = createPaymentPolicy(paymentPolicyType);
        Community community = mock(Community.class);
        when(community.getRewriteUrlParameter()).thenReturn(communityName);
        when(paymentPolicy.getCommunity()).thenReturn(community);
        return paymentPolicy;
    }

    public static PaymentPolicy createPaymentPolicy(PaymentPolicyType paymentPolicyType) {
        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicy.getPaymentPolicyType()).thenReturn(paymentPolicyType);
        return paymentPolicy;
    }

    public static PaymentPolicy paymentPolicyWithDefaultNotNullFields() {
        final PaymentPolicy paymentPolicy =
            new PaymentPolicy().withPeriod(new Period().withDuration(1).withDurationUnit(WEEKS)).withMediaType(AUDIO).withTariff(_3G).withSubCost(ONE).withDefault(true).withCommunity(new Community())
                               .withOnline(true);
        paymentPolicy.setStartDateTime(new Date(0L));
        paymentPolicy.setEndDateTime(new Date(Long.MAX_VALUE));
        return paymentPolicy;
    }
}