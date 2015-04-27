/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class O2PSMSPaymentDetailsServiceTest {
    final int RETRY_ON_ERROR = 5;

    @Mock
    UserNotificationService userNotificationService;
    @Mock
    O2PSMSPaymentDetailsInfoService o2PSMSPaymentDetailsInfoService;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PaymentPolicyRepository paymentPolicyRepository;

    @InjectMocks
    O2PSMSPaymentDetailsService o2PSMSPaymentDetailsService;

    @Mock
    User user;
    @Mock
    UserGroup userGroup;
    @Mock
    Community community;
    @Mock
    PaymentPolicy paymentPolicy;

    final String communityUrlParameter = "mtv1";

    @Before
    public void setUp() throws Exception {
        when(user.getUserGroup()).thenReturn(userGroup);
        when(userGroup.getCommunity()).thenReturn(community);
        when(community.getRewriteUrlParameter()).thenReturn(communityUrlParameter);

        o2PSMSPaymentDetailsInfoService.setRetriesOnError(RETRY_ON_ERROR);
    }

    @Test
    public void testCreatePaymentDetails() throws Exception {
        o2PSMSPaymentDetailsService.createPaymentDetails(user, paymentPolicy);

        verify(o2PSMSPaymentDetailsInfoService).createPaymentDetailsInfo(user, paymentPolicy);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }

    @Test
    public void testCreatePaymentDetailsWithNoPolicyWithContract() throws Exception {
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getProvider()).thenReturn(ProviderType.FACEBOOK);
        when(user.getSegment()).thenReturn(SegmentType.BUSINESS);
        when(user.getContract()).thenReturn(Contract.PAYG);
        when(paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(community, user.getProvider(), user.getSegment(), user.getContract(), user.getTariff())).thenReturn(paymentPolicy);

        o2PSMSPaymentDetailsService.createPaymentDetails(user);

        verify(o2PSMSPaymentDetailsInfoService).createPaymentDetailsInfo(user, paymentPolicy);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }

    @Test
    public void testCreatePaymentDetailsWithNoPolicyNoContract() throws Exception {
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getProvider()).thenReturn(ProviderType.FACEBOOK);
        when(user.getSegment()).thenReturn(SegmentType.BUSINESS);
        when(paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(community, user.getProvider(), user.getSegment(), Contract.PAYM, user.getTariff())).thenReturn(paymentPolicy);

        o2PSMSPaymentDetailsService.createPaymentDetails(user);

        verify(o2PSMSPaymentDetailsInfoService).createPaymentDetailsInfo(user, paymentPolicy);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }
}