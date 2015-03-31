package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PendingPaymentFactory {

    public static PendingPayment createPendingPayment() {
        return new PendingPayment();
    }

    public static PendingPayment create(PayPalPaymentDetails paymentDetails, User user, BigDecimal amount, String currencyISO) {
        PendingPayment pendingPayment = mock(PendingPayment.class);
        when(pendingPayment.getI()).thenReturn(1L);
        when(pendingPayment.getAmount()).thenReturn(amount);
        when(pendingPayment.getCurrencyISO()).thenReturn(currencyISO);
        when(pendingPayment.getUser()).thenReturn(user);
        when(pendingPayment.getPaymentDetails()).thenReturn(paymentDetails);
        return pendingPayment;
    }
}