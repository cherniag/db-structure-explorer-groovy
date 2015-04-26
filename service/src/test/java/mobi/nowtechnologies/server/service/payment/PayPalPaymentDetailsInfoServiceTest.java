/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PayPalPaymentDetailsInfoServiceTest {
    final int RETRY_ON_ERROR = 5;

    @Mock
    PaymentDetailsService paymentDetailsService;
    @Mock
    UserRepository userRepository;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PromotionService promotionService;
    @InjectMocks
    PayPalPaymentDetailsInfoService paymentTimeService;

    @Mock
    User user;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    PayPalResponse response;

    @Captor
    ArgumentCaptor<PayPalPaymentDetails> paymentDetailsArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        paymentTimeService.setRetriesOnError(RETRY_ON_ERROR);

        when(paymentDetailsRepository.save(any(PayPalPaymentDetails.class))).thenAnswer(new Answer<PayPalPaymentDetails>() {
            @Override
            public PayPalPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (PayPalPaymentDetails) args[0];
            }
        });
    }

    @Test
    public void testCommitPaymentDetailsForNotLimitedUser() throws Exception {
        //
        // given
        //
        final String agreement = "agreement";
        final String token = "token";
        final String payerId = "payerId";

        when(user.isLimited()).thenReturn(false);
        when(response.getBillingAgreement()).thenReturn(agreement);
        when(response.getToken()).thenReturn(token);
        when(response.getPayerId()).thenReturn(payerId);

        //
        // when
        //
        paymentTimeService.createPaymentDetailsInfo(user, paymentPolicy, response);

        //
        // then
        //
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(paymentDetailsRepository).save(paymentDetailsArgumentCaptor.capture());
        verify(userRepository).save(user);
        verifyZeroInteractions(promotionService);

        assertEquals(agreement, paymentDetailsArgumentCaptor.getValue().getBillingAgreementTxId());
        assertEquals(token, paymentDetailsArgumentCaptor.getValue().getToken());
        assertEquals(payerId, paymentDetailsArgumentCaptor.getValue().getPayerId());
        assertEquals(RETRY_ON_ERROR, paymentDetailsArgumentCaptor.getValue().getRetriesOnError());

        verify(user).setCurrentPaymentDetails(paymentDetailsArgumentCaptor.getValue());
    }

    @Test
    public void testCommitPaymentDetailsForLimitedUser() throws Exception {
        //
        // given
        //
        final String agreement = "agreement";
        final String token = "token";
        final String payerId = "payerId";

        when(user.isLimited()).thenReturn(true);
        when(response.getBillingAgreement()).thenReturn(agreement);
        when(response.getToken()).thenReturn(token);
        when(response.getPayerId()).thenReturn(payerId);

        //
        // when
        //
        paymentTimeService.createPaymentDetailsInfo(user, paymentPolicy, response);

        //
        // then
        //
        verify(promotionService).applyPromoToLimitedUser(user);
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(paymentDetailsRepository).save(paymentDetailsArgumentCaptor.capture());
        verify(userRepository).save(user);
        verifyZeroInteractions(promotionService);

        assertEquals(agreement, paymentDetailsArgumentCaptor.getValue().getBillingAgreementTxId());
        assertEquals(token, paymentDetailsArgumentCaptor.getValue().getToken());
        assertEquals(payerId, paymentDetailsArgumentCaptor.getValue().getPayerId());
        assertEquals(RETRY_ON_ERROR, paymentDetailsArgumentCaptor.getValue().getRetriesOnError());

        verify(user).setCurrentPaymentDetails(paymentDetailsArgumentCaptor.getValue());
    }


}