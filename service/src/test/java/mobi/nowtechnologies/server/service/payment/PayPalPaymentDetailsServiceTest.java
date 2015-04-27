/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;

import java.math.BigDecimal;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PayPalPaymentDetailsServiceTest {
    final String redirectURL = "redirectURL";

    @Mock
    UserNotificationService userNotificationService;
    @Mock
    UserRepository userRepository;
    @Mock
    PaymentPolicyRepository paymentPolicyRepository;
    @Mock
    PayPalPaymentDetailsInfoService payPalPaymentDetailsInfoService;
    @Mock
    PayPalHttpService httpService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    PayPalPaymentDetailsService payPalPaymentDetailsService;

    @Mock
    User user;
    @Mock
    UserGroup userGroup;
    @Mock
    Community community;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    PayPalResponse payPalResponse;
    @Mock
    PayPalPaymentDetails payPalPaymentDetails;
    @Mock
    PayPalPaymentDetailsService.PayPalDetailsInfo payPalDetailsInfo;

    final String billingDescription = "billingDescription";
    final String successUrl = "successUrl";
    final String failUrl = "failUrl";
    final String token = "token";

    final String communityUrlParameter = "mtv1";
    final String currencyISO = "GBP";
    final BigDecimal subCost = BigDecimal.TEN;

    @Before
    public void setUp() throws Exception {
        payPalPaymentDetailsService.setRedirectURL(redirectURL);

        when(payPalDetailsInfo.getBillingAgreementDescription()).thenReturn(billingDescription);
        when(payPalDetailsInfo.getSuccessUrl()).thenReturn(successUrl);
        when(payPalDetailsInfo.getFailUrl()).thenReturn(failUrl);

        when(user.getUserGroup()).thenReturn(userGroup);
        when(userGroup.getCommunity()).thenReturn(community);
        when(community.getRewriteUrlParameter()).thenReturn(communityUrlParameter);

        when(paymentPolicy.getCurrencyISO()).thenReturn(currencyISO);
        when(paymentPolicy.getSubcost()).thenReturn(subCost);

        when(payPalResponse.getToken()).thenReturn(token);
    }

    //
    // Test get Redirect URL
    //
    @Test
    public void testGetRedirectUrl_ForMissedPaymentPolicy() throws Exception {
        final int paymentPolicyId = 2;
        when(paymentPolicyRepository.findOne(paymentPolicyId)).thenReturn(null);

        String redirectUrl = payPalPaymentDetailsService.getRedirectUrl(1, paymentPolicyId, null);

        assertNull(redirectUrl);
    }

    @Test
    public void testGetRedirectUrl_ForONETIMEAndSuccessful() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.ONETIME, true);
        when(httpService.getTokenForOnetimeType(successUrl, failUrl, currencyISO, communityUrlParameter, paymentPolicy.getSubcost())).thenReturn(payPalResponse);

        String redirectUrl = payPalPaymentDetailsService.getRedirectUrl(userId, paymentPolicyId, payPalDetailsInfo);

        assertEquals(redirectURL + "?cmd=_express-checkout&useraction=commit&token=" + token, redirectUrl);
    }

    @Test
    public void testGetRedirectUrl_ForONETIMEAndFail() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.ONETIME, false);
        when(httpService.getTokenForOnetimeType(successUrl, failUrl, currencyISO, communityUrlParameter, paymentPolicy.getSubcost())).thenReturn(payPalResponse);

        thrown.expect(ServiceException.class);

        payPalPaymentDetailsService.getRedirectUrl(userId, paymentPolicyId, payPalDetailsInfo);
    }

    @Test
    public void testGetRedirectUrl_ForRECURRENTAndSuccessful() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.RECURRENT, true);
        when(httpService.getTokenForRecurrentType(successUrl, failUrl, currencyISO, communityUrlParameter, billingDescription)).thenReturn(payPalResponse);

        String redirectUrl = payPalPaymentDetailsService.getRedirectUrl(userId, paymentPolicyId, payPalDetailsInfo);

        assertEquals(redirectURL + "?cmd=_express-checkout&useraction=commit&token=" + token, redirectUrl);
    }

    @Test
    public void testGetRedirectUrl_ForRECURRENTAndFail() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.RECURRENT, false);
        when(httpService.getTokenForRecurrentType(successUrl, failUrl, currencyISO, communityUrlParameter, billingDescription)).thenReturn(payPalResponse);

        thrown.expect(ServiceException.class);

        payPalPaymentDetailsService.getRedirectUrl(userId, paymentPolicyId, payPalDetailsInfo);
    }

    //
    // Test commit payment details
    //
    @Test
    public void testCommitPaymentDetails_ForONETIMEAndSuccessful() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.ONETIME, true);
        when(httpService.getPaymentDetailsInfoForOnetimeType(token, communityUrlParameter)).thenReturn(payPalResponse);
        when(payPalPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, payPalResponse)).thenReturn(payPalPaymentDetails);

        PayPalPaymentDetails payPalPaymentDetails = payPalPaymentDetailsService.createPaymentDetails(userId, paymentPolicyId, token);

        assertSame(this.payPalPaymentDetails, payPalPaymentDetails);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }

    @Test
    public void testCommitPaymentDetails_ForONETIMEAndFail() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.ONETIME, false);
        when(httpService.getPaymentDetailsInfoForOnetimeType(token, communityUrlParameter)).thenReturn(payPalResponse);
        when(payPalPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, payPalResponse)).thenReturn(payPalPaymentDetails);

        thrown.expect(ServiceException.class);
        payPalPaymentDetailsService.createPaymentDetails(userId, paymentPolicyId, token);
        verifyZeroInteractions(userNotificationService);
    }

    @Test
    public void testCommitPaymentDetails_ForRECURRENTAndSuccessful() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.RECURRENT, true);
        when(httpService.getPaymentDetailsInfoForRecurrentType(token, communityUrlParameter)).thenReturn(payPalResponse);
        when(payPalPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, payPalResponse)).thenReturn(payPalPaymentDetails);

        PayPalPaymentDetails payPalPaymentDetails = payPalPaymentDetailsService.createPaymentDetails(userId, paymentPolicyId, token);

        assertSame(this.payPalPaymentDetails, payPalPaymentDetails);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }

    @Test
    public void testCommitPaymentDetails_ForRECURRENTAndFail() throws Exception {
        final int userId = 1;
        final int paymentPolicyId = 2;
        mockToGetRedirectURL(userId, paymentPolicyId, PaymentPolicyType.RECURRENT, false);
        when(httpService.getPaymentDetailsInfoForRecurrentType(token, communityUrlParameter)).thenReturn(payPalResponse);
        when(payPalPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, payPalResponse)).thenReturn(payPalPaymentDetails);

        thrown.expect(ServiceException.class);
        payPalPaymentDetailsService.createPaymentDetails(userId, paymentPolicyId, token);
        verifyZeroInteractions(userNotificationService);
    }


    private void mockToGetRedirectURL(int userId, int paymentPolicyId, PaymentPolicyType policyType, boolean isSuccessfulResponse) {
        when(payPalResponse.isSuccessful()).thenReturn(isSuccessfulResponse);
        when(paymentPolicyRepository.findOne(paymentPolicyId)).thenReturn(paymentPolicy);
        when(userRepository.findOne(userId)).thenReturn(user);
        when(paymentPolicy.getPaymentPolicyType()).thenReturn(policyType);
    }
}