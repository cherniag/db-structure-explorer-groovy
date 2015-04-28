/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SagePayPaymentDetailsServiceTest {
    final String VENDOR_TX_CODE = "vendorTxCode";

    @Mock
    UserNotificationService userNotificationService;
    @Mock
    SagePayHttpService sagePayHttpService;
    @Mock
    SagePayPaymentDetailsInfoService sagePayPaymentDetailsInfoService;

    @InjectMocks
    SagePayPaymentDetailsService sagePayPaymentDetailsService;

    @Mock
    User user;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    PaymentDetailsDto pdto;
    @Mock
    SagePayResponse sagePayResponse;
    @Mock
    SagePayCreditCardPaymentDetails sagePayCreditCardPaymentDetails;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreatePaymentDetails_Success() throws Exception {
        //
        // given
        //
        when(pdto.getVendorTxCode()).thenReturn(VENDOR_TX_CODE);
        when(sagePayResponse.isSuccessful()).thenReturn(true);
        when(sagePayHttpService.makeDeferRequest(pdto)).thenReturn(sagePayResponse);
        when(sagePayPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, sagePayResponse, VENDOR_TX_CODE)).thenReturn(sagePayCreditCardPaymentDetails);
        //
        // when
        //
        SagePayCreditCardPaymentDetails paymentDetails = sagePayPaymentDetailsService.createPaymentDetails(pdto, user, paymentPolicy);

        //
        // then
        //
        assertSame(paymentDetails, sagePayCreditCardPaymentDetails);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }

    @Test
    public void testCreatePaymentDetails_Fail() throws Exception {
        //
        // given
        //
        when(pdto.getVendorTxCode()).thenReturn(VENDOR_TX_CODE);
        when(sagePayResponse.isSuccessful()).thenReturn(false);
        when(sagePayHttpService.makeDeferRequest(pdto)).thenReturn(sagePayResponse);
        when(sagePayPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, sagePayResponse, VENDOR_TX_CODE)).thenReturn(sagePayCreditCardPaymentDetails);

        //
        // when
        //
        thrown.expect(ServiceException.class);
        SagePayCreditCardPaymentDetails paymentDetails = sagePayPaymentDetailsService.createPaymentDetails(pdto, user, paymentPolicy);
    }
}