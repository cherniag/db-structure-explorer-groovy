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

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VFPSMSPaymentDetailsServiceTest {
    final int RETRY_ON_ERROR = 5;

    @Mock
    UserNotificationService userNotificationService;
    @Mock
    VFPSMSPaymentDetailsInfoService vfpsmsPaymentDetailsInfoService;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PaymentPolicyRepository paymentPolicyRepository;

    @InjectMocks
    VFPSMSPaymentDetailsService vfpsmsPaymentDetailsService;

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

        vfpsmsPaymentDetailsInfoService.setRetriesOnError(RETRY_ON_ERROR);
    }

    @Test
    public void testCreatePaymentDetails() throws Exception {
        vfpsmsPaymentDetailsService.createPaymentDetails(user, paymentPolicy);

        verify(vfpsmsPaymentDetailsInfoService).createPaymentDetailsInfo(user, paymentPolicy);
        verify(userNotificationService).sendSubscriptionChangedSMS(user);
    }
}