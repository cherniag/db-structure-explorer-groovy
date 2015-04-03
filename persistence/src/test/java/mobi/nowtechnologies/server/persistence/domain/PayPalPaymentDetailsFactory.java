/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PayPalPaymentDetailsFactory {
    public static PayPalPaymentDetails createPayPalPaymentDetails(PaymentPolicy paymentPolicy, String payerid, String token, String billingAgreementId) {
        PayPalPaymentDetails paymentDetails = mock(PayPalPaymentDetails.class);
        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(paymentDetails.getPayerId()).thenReturn(payerid);
        when(paymentDetails.getToken()).thenReturn(token);
        when(paymentDetails.getBillingAgreementTxId()).thenReturn(billingAgreementId);
        return paymentDetails;
    }
}
