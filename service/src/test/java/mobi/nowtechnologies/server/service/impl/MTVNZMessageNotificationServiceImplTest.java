package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Arrays;
import java.util.Locale;

import org.joda.time.DateTime;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MTVNZMessageNotificationServiceImplTest {

    @Mock
    CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;

    @InjectMocks
    MTVNZMessageNotificationServiceImpl mtvnzMessageNotificationServiceImpl;

    @Test
    public void shouldReturnUnsubscriptionMessageForFreeTrialMtvnzUserWithVFCurrentPaymentDetailsWhenUserUnsubscribeManuallyViaWebOrStopSms() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);

        PaymentPolicy paymentPolicy = new PaymentPolicy();

        PaymentDetails vfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        vfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        user.setCurrentPaymentDetails(vfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.after.text";

        String expectedMsg = "You have successfully unsubscribed from MTV Trax. If you change your mind, subscribe again via the account page in the app.";
        final String expectedMsgCode = "sms.unsubscribe.after.text.for.vfPsms.onFreeTrial.user";

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase,new String[]{"http://short.link", "0"});

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void shouldReturnUnsubscriptionMessageForPayedMtvnzUserWithVFCurrentPaymentDetailsWhenUserUnsubscribeManuallyViaWebOrStopSms() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(0L);
        user.setNextSubPayment((int) new DateTime().plusDays(1).getMillis());

        PaymentPolicy paymentPolicy = new PaymentPolicy();

        PaymentDetails vfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        vfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        user.setCurrentPaymentDetails(vfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.after.text";

        String expectedMsg = "You have successfully unsubscribed from MTV Trax and have 1 days left of full access. If you change your mind, subscribe again via the account page in the app.";
        final String expectedMsgCode = "sms.unsubscribe.after.text.for.vfPsms.onBoughtPeriod.user";

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase, new String[]{"http://short.link", "1"});

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void shouldReturnPaymentDetailsChangingMessageForFreeTrialMtvnzUserWithVFCurrentPaymentDetailsWhenUserChangesPaymentPolicy() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);

        PaymentPolicy newPaymentPolicy = new PaymentPolicy();
        newPaymentPolicy.setId(1);

        PaymentPolicy prevPaymentPolicy = new PaymentPolicy();
        prevPaymentPolicy.setId(2);

        PaymentDetails newVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        newVfPsmsPaymentDetails.setPaymentPolicy(newPaymentPolicy);

        PaymentDetails prevVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        prevVfPsmsPaymentDetails.setPaymentPolicy(prevPaymentPolicy);

        user.setPaymentDetailsList(Arrays.asList(prevVfPsmsPaymentDetails, newVfPsmsPaymentDetails));
        user.setCurrentPaymentDetails(newVfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.potential.text";

        String expectedMsg = "Your MTV Trax subscription has changed to $1 per week. Full tracks, unlimited plays, overnight updates and no ads. To unsubscribe text STOP to 3140";
        final String expectedMsgCode = "sms.unsubscribe.potential.text.for.vfPsms.user.prevPaymentPolicyIsDiffer";
        String[] msgArgs = {"http://short.link", "$", "1", "per week", "3140"};

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase, msgArgs);

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null));
    }

    @Test
    public void shouldReturnPaymentDetailsChangingMessageForPayedMtvnzUserWithVFCurrentPaymentDetailsWhenUserChangesPaymentPolicy() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(0L);
        user.setNextSubPayment((int) new DateTime().plusDays(1).getMillis());

        PaymentPolicy newPaymentPolicy = new PaymentPolicy();
        newPaymentPolicy.setId(1);

        PaymentPolicy prevPaymentPolicy = new PaymentPolicy();
        prevPaymentPolicy.setId(2);

        PaymentDetails newVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        newVfPsmsPaymentDetails.setPaymentPolicy(newPaymentPolicy);

        PaymentDetails prevVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        prevVfPsmsPaymentDetails.setPaymentPolicy(prevPaymentPolicy);

        user.setPaymentDetailsList(Arrays.asList(prevVfPsmsPaymentDetails, newVfPsmsPaymentDetails));
        user.setCurrentPaymentDetails(newVfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.potential.text";

        String expectedMsg = "Your MTV Trax subscription has changed to $1 per week. Full tracks, unlimited plays, overnight updates and no ads. To unsubscribe text STOP to 3140";
        final String expectedMsgCode = "sms.unsubscribe.potential.text.for.vfPsms.user.prevPaymentPolicyIsDiffer";
        String[] msgArgs = {"http://short.link", "$", "1", "per week", "3140"};

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase, msgArgs);

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null));
    }

    @Test
    public void shouldReturnPaymentDetailsChangingMessageForFreeTrialMtvnzUserWithVFCurrentPaymentDetailsWhenUserChangePaymentDetailsButNotPaymentPolicy() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);

        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setId(1);

        PaymentDetails newVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        newVfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        PaymentDetails prevVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        prevVfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        user.setPaymentDetailsList(Arrays.asList(prevVfPsmsPaymentDetails, newVfPsmsPaymentDetails));
        user.setCurrentPaymentDetails(newVfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.potential.text";

        String expectedMsg = "The mobile number linked to your MTV Trax subscription has successfully been changed. To unsubscribe, text STOP to 3140";
        final String expectedMsgCode = "sms.unsubscribe.potential.text.for.vfPsms.user.prevPaymentPolicyIsTheSame";
        String[] msgArgs = {"http://short.link", "$", "1", "per week", "3140"};

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase, msgArgs);

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null));
    }

    @Test
    public void shouldReturnPaymentDetailsChangingMessageForPayedMtvnzUserWithVFCurrentPaymentDetailsWhenUserChangePaymentDetailsButNotPaymentPolicy() throws Exception {
        //given
        final String rewriteUrlParameter = "mtvnz";

        Community mtvnzCommunity = CommunityFactory.createCommunity();
        mtvnzCommunity.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup mtvnzUserGroup = UserGroupFactory.createUserGroup(mtvnzCommunity);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(mtvnzUserGroup);
        user.setProvider(ProviderType.FACEBOOK);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(DeviceTypeFactory.createDeviceType("ANDROID"));
        user.setFreeTrialExpiredMillis(0L);
        user.setNextSubPayment((int) new DateTime().plusDays(1).getMillis());

        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setId(1);

        PaymentDetails newVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        newVfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        PaymentDetails prevVfPsmsPaymentDetails = new VFPSMSPaymentDetails();
        prevVfPsmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        user.setPaymentDetailsList(Arrays.asList(prevVfPsmsPaymentDetails, newVfPsmsPaymentDetails));
        user.setCurrentPaymentDetails(newVfPsmsPaymentDetails);

        String msgCodeBase = "sms.unsubscribe.potential.text";

        String expectedMsg = "The mobile number linked to your MTV Trax subscription has successfully been changed. To unsubscribe, text STOP to 3140";
        final String expectedMsgCode = "sms.unsubscribe.potential.text.for.vfPsms.user.prevPaymentPolicyIsTheSame";
        String[] msgArgs = {"http://short.link", "$", "1", "per week", "3140"};

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        //when
        String result = mtvnzMessageNotificationServiceImpl.getMessage(user, msgCodeBase, msgArgs);

        //then
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), eq(msgArgs), eq(""), eq((Locale) null));
    }
}