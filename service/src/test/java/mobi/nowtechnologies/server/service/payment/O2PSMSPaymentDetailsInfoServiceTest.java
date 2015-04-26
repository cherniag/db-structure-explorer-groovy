/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class O2PSMSPaymentDetailsInfoServiceTest {
    final int RETRY_ON_ERROR = 5;
    final String MOBILE = "+044444444444";

    @Mock
    UserRepository userRepository;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Mock
    PaymentDetailsService paymentDetailsService;
    @Mock
    PromotionService promotionService;

    @InjectMocks
    O2PSMSPaymentDetailsInfoService o2PSMSPaymentDetailsInfoService;

    @Mock
    User user;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    Promotion promotion;
    @Mock
    PromotionPaymentPolicy promotionPaymentPolicy;
    @Captor
    ArgumentCaptor<O2PSMSPaymentDetails> paymentDetailsArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        o2PSMSPaymentDetailsInfoService.setRetriesOnError(RETRY_ON_ERROR);
    }

    @Test
    public void testCreatePaymentDetails_WithNoPotentialPromotion() throws Exception {
        //
        // given
        //
        when(user.getMobile()).thenReturn(MOBILE);

        //
        // when
        //
        o2PSMSPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy);

        //
        // then
        //
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(paymentDetailsRepository).save(paymentDetailsArgumentCaptor.capture());
        verify(userRepository).save(user);
        verifyZeroInteractions(promotionService);

        assertSame(user, paymentDetailsArgumentCaptor.getValue().getOwner());
        assertEquals(MOBILE, paymentDetailsArgumentCaptor.getValue().getPhoneNumber());
        assertEquals(RETRY_ON_ERROR, paymentDetailsArgumentCaptor.getValue().getRetriesOnError());
        assertEquals(paymentPolicy, paymentDetailsArgumentCaptor.getValue().getPaymentPolicy());

        verify(user).setCurrentPaymentDetails(paymentDetailsArgumentCaptor.getValue());
    }

    @Test
    public void testCreatePaymentDetails_WithPotentialPromotion() throws Exception {
        //
        // given
        //
        when(user.getMobile()).thenReturn(MOBILE);
        when(user.getPotentialPromotion()).thenReturn(promotion);
        when(promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy)).thenReturn(promotionPaymentPolicy);

        //
        // when
        //
        o2PSMSPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy);

        //
        // then
        //
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(paymentDetailsRepository).save(paymentDetailsArgumentCaptor.capture());
        verify(userRepository).save(user);
        verify(promotionService).incrementUserNumber(promotion);

        assertSame(user, paymentDetailsArgumentCaptor.getValue().getOwner());
        assertSame(promotionPaymentPolicy, paymentDetailsArgumentCaptor.getValue().getPromotionPaymentPolicy());
        assertEquals(MOBILE, paymentDetailsArgumentCaptor.getValue().getPhoneNumber());
        assertEquals(RETRY_ON_ERROR, paymentDetailsArgumentCaptor.getValue().getRetriesOnError());
        assertEquals(paymentPolicy, paymentDetailsArgumentCaptor.getValue().getPaymentPolicy());

        verify(user).setCurrentPaymentDetails(paymentDetailsArgumentCaptor.getValue());
    }
}