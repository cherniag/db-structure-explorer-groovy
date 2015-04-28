/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
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
public class SagePayPaymentDetailsInfoServiceTest {
    final int RETRY_ON_ERROR = 5;

    final String SECURITY_KEY = "securityKey";
    final String TX_AUTH_NO = "txAuthNo";
    final String VPS_TX_ID = "vpsTxId";

    final String VENDOR_TX_CODE = "vendorTxCode";

    @Mock
    PromotionService promotionService;
    @Mock
    UserRepository userRepository;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Mock
    PaymentDetailsService paymentDetailsService;

    @InjectMocks
    SagePayPaymentDetailsInfoService sagePayPaymentDetailsInfoService;

    @Mock
    User user;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    Promotion promotion;
    @Mock
    PromotionPaymentPolicy promotionPaymentPolicy;
    @Mock
    SagePayCreditCardPaymentDetails.DetailsInfo info;
    @Captor
    ArgumentCaptor<SagePayCreditCardPaymentDetails> paymentDetailsArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        sagePayPaymentDetailsInfoService.setRetriesOnError(RETRY_ON_ERROR);
    }

    @Test
    public void testCreatePaymentDetailsInfo() throws Exception {
        //
        // given
        //
        when(info.getSecurityKey()).thenReturn(SECURITY_KEY);
        when(info.getTxAuthNo()).thenReturn(TX_AUTH_NO);
        when(info.getVPSTxId()).thenReturn(VPS_TX_ID);

        when(user.isLimited()).thenReturn(true);
        when(user.getPotentialPromotion()).thenReturn(promotion);
        when(promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy)).thenReturn(promotionPaymentPolicy);

        //
        // when
        //
        sagePayPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, info, VENDOR_TX_CODE);

        //
        // then
        //
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(promotionService).applyPromoToLimitedUser(user);
        verify(promotionService).incrementUserNumber(promotion);
        verify(paymentDetailsRepository).save(paymentDetailsArgumentCaptor.capture());
        verify(userRepository).save(user);

        assertEquals(TX_AUTH_NO, paymentDetailsArgumentCaptor.getValue().getTxAuthNo());
        assertEquals(VPS_TX_ID, paymentDetailsArgumentCaptor.getValue().getVPSTxId());
        assertEquals(SECURITY_KEY, paymentDetailsArgumentCaptor.getValue().getSecurityKey());

        assertEquals(VENDOR_TX_CODE, paymentDetailsArgumentCaptor.getValue().getVendorTxCode());
        assertEquals(RETRY_ON_ERROR, paymentDetailsArgumentCaptor.getValue().getRetriesOnError());
        assertSame(user, paymentDetailsArgumentCaptor.getValue().getOwner());
        assertSame(paymentPolicy, paymentDetailsArgumentCaptor.getValue().getPaymentPolicy());
        assertSame(promotionPaymentPolicy, paymentDetailsArgumentCaptor.getValue().getPromotionPaymentPolicy());
    }
}