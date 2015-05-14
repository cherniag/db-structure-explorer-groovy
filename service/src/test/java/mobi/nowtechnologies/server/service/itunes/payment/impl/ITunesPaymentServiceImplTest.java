/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.payment.impl;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentLock;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.ITunesPaymentLockRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;

import org.junit.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ITunesPaymentServiceImplTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @Mock
    PaymentPolicyService paymentPolicyService;
    @Mock
    SubmittedPaymentService submittedPaymentService;
    @Mock
    ITunesPaymentLockRepository iTunesPaymentLockRepository;
    @Mock
    TimeService timeService;

    @InjectMocks
    ITunesPaymentServiceImpl iTunesPaymentService;

    private static void validatePayment(final String base64EncodedAppStoreReceipt, final String originalTransactionId, final long expiresDate, final String appStoreOriginalTransactionId,
                                        final User user, final BigDecimal paymentPolicySubCost, final String currencyISO, final long paymentTimestamp, final PaymentDetailsType paymentType,
                                        SubmittedPayment passedSubmittedPayment) {

        assertEquals(PaymentDetailsStatus.SUCCESSFUL, passedSubmittedPayment.getStatus());
        assertEquals(user, passedSubmittedPayment.getUser());
        assertEquals(paymentTimestamp, passedSubmittedPayment.getTimestamp());
        assertEquals(paymentPolicySubCost, passedSubmittedPayment.getAmount());
        assertEquals(originalTransactionId, passedSubmittedPayment.getExternalTxId());
        assertEquals(paymentType, passedSubmittedPayment.getType());
        assertEquals(currencyISO, passedSubmittedPayment.getCurrencyISO());
        assertEquals((int) (expiresDate / 1000), passedSubmittedPayment.getNextSubPayment());
        assertEquals(appStoreOriginalTransactionId, passedSubmittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, passedSubmittedPayment.getPaymentSystem());
        assertEquals(base64EncodedAppStoreReceipt, passedSubmittedPayment.getBase64EncodedAppStoreReceipt());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSubmittedPayment_Success() throws Exception {
        final String receipt = "receipt";

        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity());

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setCurrentPaymentDetails(null);
        user.setUserGroup(userGroup);
        user.setAppStoreOriginalTransactionId("1000000064861007");

        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setSubcost(BigDecimal.TEN);
        paymentPolicy.setCurrencyISO("GBP");

        final PaymentDetailsType paymentType = PaymentDetailsType.FIRST;

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.getProductId()).thenReturn("getProductId");
        when(iTunesResult.getOriginalTransactionId()).thenReturn(user.getAppStoreOriginalTransactionId());
        when(iTunesResult.getExpireTime()).thenReturn(DateTimeUtils.moveDate(new Date(), DateTimeUtils.UTC_TIME_ZONE_ID, 1, DurationUnit.DAYS).getTime());

        when(paymentPolicyService.findByCommunityAndAppStoreProductId(user.getCommunity(), iTunesResult.getProductId())).thenReturn(paymentPolicy);
        when(timeService.now()).thenReturn(new Date(Long.MAX_VALUE));
        doAnswer(new ITunesPaymentLockAnswer(user.getId(), DateTimeUtils.millisToIntSeconds(iTunesResult.getExpireTime()))).
                                                                                                                               when(iTunesPaymentLockRepository)
                                                                                                                           .saveAndFlush(any(ITunesPaymentLock.class));
        doAnswer(
            new SubmittedPaymentServiceAnswer(receipt, timeService.now().getTime(), paymentPolicy.getCurrencyISO(), user.getAppStoreOriginalTransactionId(), paymentType, paymentPolicy.getSubcost(),
                                              user, iTunesResult.getOriginalTransactionId(), iTunesResult.getExpireTime())).
                                                                                                                               when(submittedPaymentService).save(any(SubmittedPayment.class));
        doAnswer(new PaymentEventAnswer(iTunesResult.getExpireTime(), timeService.now().getTime(), user, receipt, user.getAppStoreOriginalTransactionId(), paymentPolicy.getCurrencyISO(),
                                        iTunesResult.getOriginalTransactionId(), paymentType, paymentPolicy.getSubcost())).
                                                                                                                              when(applicationEventPublisher).publishEvent(any(PaymentEvent.class));

        iTunesPaymentService.createSubmittedPayment(user, receipt, iTunesResult);

        verify(iTunesPaymentLockRepository, times(1)).saveAndFlush(any(ITunesPaymentLock.class));
        verify(paymentPolicyService, times(1)).findByCommunityAndAppStoreProductId(user.getCommunity(), iTunesResult.getProductId());
        verify(timeService, times(3)).now();
        verify(submittedPaymentService, times(1)).save(any(SubmittedPayment.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(PaymentEvent.class));
        verifyNoMoreInteractions(iTunesPaymentLockRepository, paymentPolicyService, timeService, submittedPaymentService, applicationEventPublisher);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void processInCaseOfDuplicates() throws Exception {
        final String base64EncodedAppStoreReceipt = "SOME_RECEIPT";
        final User user = getMockForEligibleUser(100, mock(Community.class));

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.getExpireTime()).thenReturn(DateTimeUtils.moveDate(new Date(), DateTimeUtils.UTC_TIME_ZONE_ID, 1, DurationUnit.DAYS).getTime());
        when(iTunesPaymentLockRepository.saveAndFlush(any(ITunesPaymentLock.class))).thenThrow(new DataIntegrityViolationException(""));

        iTunesPaymentService.createSubmittedPayment(user, base64EncodedAppStoreReceipt, iTunesResult);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void processInCaseOfDuplicates_ExpireTimeIsNull() throws Exception {
        final String base64EncodedAppStoreReceipt = "SOME_RECEIPT";
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(10);
        final User user = getMockForEligibleUser(100, community);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.getProductId()).thenReturn("getProductId");
        when(iTunesResult.getExpireTime()).thenReturn(null);
        when(iTunesResult.getPurchaseTime()).thenReturn(DateTimeUtils.moveDate(new Date(), DateTimeUtils.UTC_TIME_ZONE_ID, 1, DurationUnit.DAYS).getTime());

        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicy.getPeriod()).thenReturn(new Period(DurationUnit.DAYS, 5));

        when(paymentPolicyService.findByCommunityAndAppStoreProductId(user.getCommunity(), iTunesResult.getProductId())).thenReturn(paymentPolicy);
        when(iTunesPaymentLockRepository.saveAndFlush(any(ITunesPaymentLock.class))).thenThrow(new DataIntegrityViolationException(""));

        /**
         *
         logger.debug("Calculate expire timestamp for result {} and community id {}", result, community.getId());
         PaymentPolicy policy = paymentPolicyService.findByCommunityAndAppStoreProductId(community, result.getProductId());
         Period period = policy.getPeriod();
         int purchaseSeconds = DateTimeUtils.millisToIntSeconds(result.getPurchaseTime());
         int nextSubPaymentSeconds = period.toNextSubPaymentSeconds(purchaseSeconds);
         time = DateTimeUtils.secondsToMillis(nextSubPaymentSeconds);
         */
        iTunesPaymentService.createSubmittedPayment(user, base64EncodedAppStoreReceipt, iTunesResult);
    }

    @Test
    public void testGetCurrentSubscribedPaymentPolicy() throws Exception {
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(10);
        final User user = getMockForEligibleUser(100, community);
        SubmittedPayment submittedPayment = mock(SubmittedPayment.class);

        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(submittedPayment.getNextSubPayment()).thenReturn((int) (new Date().getTime() / 1000 + 10000));
        when(submittedPayment.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(submittedPaymentService.getLatest(user)).thenReturn(submittedPayment);
        when(timeService.now()).thenReturn(new Date());

        PaymentPolicy actual = iTunesPaymentService.getCurrentSubscribedPaymentPolicy(user);

        assertEquals(paymentPolicy, actual);
    }

    @Test
    public void testGetCurrentSubscribedPaymentPolicyForOldPayment() throws Exception {
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(10);
        final User user = getMockForEligibleUser(100, community);
        SubmittedPayment submittedPayment = mock(SubmittedPayment.class);

        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(submittedPayment.getNextSubPayment()).thenReturn((int) (new Date().getTime() / 1000 - 10000));
        when(submittedPayment.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(submittedPaymentService.getLatest(user)).thenReturn(submittedPayment);
        when(timeService.nowSeconds()).thenReturn((int) (new Date().getTime() / 1000));

        PaymentPolicy actual = iTunesPaymentService.getCurrentSubscribedPaymentPolicy(user);

        assertNull(actual);
    }

    private User getMockForEligibleUser(int userId, Community community) {
        User user = mock(User.class);
        UserGroup userGroup = mock(UserGroup.class);
        when(user.getUserGroup()).thenReturn(userGroup);
        when(userGroup.getCommunity()).thenReturn(community);
        when(user.getCommunityRewriteUrl()).thenReturn("nowtop40");
        when(user.getId()).thenReturn(userId);
        when(user.getCommunity()).thenReturn(community);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(null);
        when(user.hasLimitedStatus()).thenReturn(true);
        return user;
    }

    private final class PaymentEventAnswer implements Answer<Void> {

        private final long expiresDate;
        private final long paymentTimestamp;
        private final User user;
        private final String base64EncodedAppStoreReceipt;
        private final String appStoreOriginalTransactionId;
        private final String currencyISO;
        private final String originalTransactionId;
        private final PaymentDetailsType paymentType;
        private final BigDecimal paymentPolicySubCost;

        private PaymentEventAnswer(long expiresDate, long paymentTimestamp, User user, String base64EncodedAppStoreReceipt, String appStoreOriginalTransactionId, String currencyISO,
                                   String originalTransactionId, PaymentDetailsType paymentType, BigDecimal paymentPolicySubCost) {
            this.expiresDate = expiresDate;
            this.paymentTimestamp = paymentTimestamp;
            this.user = user;
            this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
            this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
            this.currencyISO = currencyISO;
            this.originalTransactionId = originalTransactionId;
            this.paymentType = paymentType;
            this.paymentPolicySubCost = paymentPolicySubCost;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            PaymentEvent passedPaymentEvent = (PaymentEvent) invocation.getArguments()[0];

            SubmittedPayment passedSubmittedPayment = (SubmittedPayment) passedPaymentEvent.getPayment();

            validatePayment(base64EncodedAppStoreReceipt, originalTransactionId, expiresDate, appStoreOriginalTransactionId, user, paymentPolicySubCost, currencyISO, paymentTimestamp, paymentType,
                            passedSubmittedPayment);

            return null;
        }
    }

    private final class SubmittedPaymentServiceAnswer implements Answer<SubmittedPayment> {

        private final String base64EncodedAppStoreReceipt;
        private final long paymentTimestamp;
        private final String currencyISO;
        private final String appStoreOriginalTransactionId;
        private final PaymentDetailsType paymentType;
        private final BigDecimal paymentPolicySubCost;
        private final User user;
        private final String originalTransactionId;
        private final long expiresDate;

        private SubmittedPaymentServiceAnswer(String base64EncodedAppStoreReceipt, long paymentTimestamp, String currencyISO, String appStoreOriginalTransactionId, PaymentDetailsType paymentType,
                                              BigDecimal paymentPolicySubCost, User user, String originalTransactionId, long expiresDate) {
            this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
            this.paymentTimestamp = paymentTimestamp;
            this.currencyISO = currencyISO;
            this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
            this.paymentType = paymentType;
            this.paymentPolicySubCost = paymentPolicySubCost;
            this.user = user;
            this.originalTransactionId = originalTransactionId;
            this.expiresDate = expiresDate;
        }

        @Override
        public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
            SubmittedPayment passedSubmittedPayment = (SubmittedPayment) invocation.getArguments()[0];

            validatePayment(base64EncodedAppStoreReceipt, originalTransactionId, expiresDate, appStoreOriginalTransactionId, user, paymentPolicySubCost, currencyISO, paymentTimestamp, paymentType,
                            passedSubmittedPayment);

            return passedSubmittedPayment;
        }
    }

    private final class ITunesPaymentLockAnswer implements Answer<ITunesPaymentLock> {

        private int userId;
        private int nextSubPaymentSeconds;

        public ITunesPaymentLockAnswer(int userId, int nextSubPaymentSeconds) {
            this.userId = userId;
            this.nextSubPaymentSeconds = nextSubPaymentSeconds;
        }

        @Override
        public ITunesPaymentLock answer(InvocationOnMock invocation) throws Throwable {
            ITunesPaymentLock lock = (ITunesPaymentLock) invocation.getArguments()[0];

            assertEquals(userId, lock.getUserId());
            assertEquals(nextSubPaymentSeconds, lock.getNextSubPayment());

            return lock;
        }
    }

}