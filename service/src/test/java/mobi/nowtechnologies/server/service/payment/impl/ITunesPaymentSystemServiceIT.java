package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
/**
 * Author: Gennadii Cherniaiev Date: 4/17/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class ITunesPaymentSystemServiceIT {
    private static final int APP_STORE_OK_RESPONSE_CODE = 0;
    private static final int APP_STORE_NOT_VALID_RESPONSE_CODE = 210006;
    private String communityRewriteUrl = "mtv1";
    private String productId = "com.musicqubed.ios.mp.subscription.weekly.2";
    @Resource
    private ITunesPaymentSystemService iTunesPaymentSystemService;
    @Resource
    private UserGroupRepository userGroupRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    private PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private PendingPaymentRepository pendingPaymentRepository;
    @Resource
    private SubmittedPaymentRepository submittedPaymentRepository;
    @Resource
    private AccountLogRepository accountLogRepository;
    @Resource
    private UserStatusRepository userStatusRepository;


    @Test
    public void startSuccessfulPaymentRecurrent() throws Exception {
        final int nextSubPayment = 1523820502;
        final String transactionId = "555555555555";

        User user = createUser();
        String appStoreTransactionReceipt = createAppStoreReceipt("renewable", APP_STORE_OK_RESPONSE_CODE, productId, transactionId, nextSubPayment);
        PaymentPolicy paymentPolicy = createPaymentPolicy(PaymentPolicyType.RECURRENT, productId, new Period(WEEKS, 1));
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy, appStoreTransactionReceipt);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        User found = userRepository.findOne(user.getId());
        assertTrue(found.isSubscribedStatus());
        assertEquals(nextSubPayment, found.getNextSubPayment());
        assertEquals(transactionId, found.getAppStoreOriginalTransactionId());
        assertEquals(appStoreTransactionReceipt, found.getBase64EncodedAppStoreReceipt());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, found.getLastSubscribedPaymentSystem());

        ITunesPaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(foundPaymentDetails.isActivated());
        assertNull(foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() == 0);
        assertEquals(paymentPolicy.getId(), foundPaymentDetails.getPaymentPolicy().getId());
        assertEquals(appStoreTransactionReceipt, foundPaymentDetails.getAppStoreReceipt());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(1, submittedPayments.size());
        SubmittedPayment submittedPayment = submittedPayments.get(0);
        assertEquals(transactionId, submittedPayment.getExternalTxId());
        assertEquals(appStoreTransactionReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
        assertEquals(nextSubPayment, submittedPayment.getNextSubPayment());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertEquals(1, accountLogs.size());
        Assert.assertEquals(TransactionType.CARD_TOP_UP, accountLogs.get(0).getTransactionType());
    }

    @Test
    public void startSuccessfulPaymentOnetime() throws Exception {
        final int purchaseSeconds = 1523820502;
        final Period period = new Period(WEEKS, 1);
        final String transactionId = "555555555555";
        final String productId = "com.musicqubed.ios.mp.subscription.onetime.1";

        User user = createUser();
        String appStoreTransactionReceipt = createAppStoreReceipt("onetime", APP_STORE_OK_RESPONSE_CODE, productId, transactionId, purchaseSeconds);
        PaymentPolicy paymentPolicy = createPaymentPolicy(PaymentPolicyType.ONETIME, productId, period);
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy, appStoreTransactionReceipt);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        final int expectedNextSubPayment = period.toNextSubPaymentSeconds(purchaseSeconds);
        User found = userRepository.findOne(user.getId());
        assertTrue(found.isSubscribedStatus());
        assertEquals(expectedNextSubPayment, found.getNextSubPayment());
        assertEquals(transactionId, found.getAppStoreOriginalTransactionId());
        assertEquals(appStoreTransactionReceipt, found.getBase64EncodedAppStoreReceipt());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, found.getLastSubscribedPaymentSystem());

        ITunesPaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(foundPaymentDetails.isActivated());
        assertNull(foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() == 0);
        assertEquals(paymentPolicy.getId(), foundPaymentDetails.getPaymentPolicy().getId());
        assertEquals(appStoreTransactionReceipt, foundPaymentDetails.getAppStoreReceipt());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(1, submittedPayments.size());
        SubmittedPayment submittedPayment = submittedPayments.get(0);
        assertEquals(transactionId, submittedPayment.getExternalTxId());
        assertEquals(appStoreTransactionReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
        assertEquals(expectedNextSubPayment, submittedPayment.getNextSubPayment());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertEquals(1, accountLogs.size());
        Assert.assertEquals(TransactionType.CARD_TOP_UP, accountLogs.get(0).getTransactionType());
    }

    @Test
    public void startPaymentWithNotValidReceipt() throws Exception {
        final int nextSubPayment = 1523820502;
        final String transactionId = "555555555555";

        User user = createUser();
        String appStoreTransactionReceipt = createAppStoreReceipt("renewable", APP_STORE_NOT_VALID_RESPONSE_CODE, productId, transactionId, nextSubPayment);
        PaymentPolicy paymentPolicy = createPaymentPolicy(PaymentPolicyType.RECURRENT, productId, new Period(WEEKS, 1));
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy, appStoreTransactionReceipt);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        User found = userRepository.findOne(user.getId());
        assertFalse(found.isSubscribedStatus());
        assertTrue(Utils.getEpochSeconds() > found.getNextSubPayment());
        assertNull(found.getAppStoreOriginalTransactionId());
        assertNull(found.getBase64EncodedAppStoreReceipt());
        assertNull(found.getLastSubscribedPaymentSystem());

        ITunesPaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertFalse(foundPaymentDetails.isActivated());
        assertEquals("Not valid receipt, status " + APP_STORE_NOT_VALID_RESPONSE_CODE, foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() > 0);

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(0, submittedPayments.size());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertTrue(accountLogs.isEmpty());
    }

    @After
    public void tearDown() throws Exception {
        accountLogRepository.deleteAll();
        submittedPaymentRepository.deleteAll();
    }

    private String createAppStoreReceipt(String renewable, int appStoreResponseCode, String productId, String transactionId, int nextSubPayment) {
        return String.format("%s:200:%s:%s:%s:%s000", renewable, appStoreResponseCode, productId, transactionId, nextSubPayment);
    }

    private PendingPayment createPendingPayment(User user, PaymentDetails paymentDetails) {
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setType(PaymentDetailsType.FIRST);
        pendingPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        pendingPayment.setPeriod(paymentDetails.getPaymentPolicy().getPeriod());
        pendingPayment.setAmount(paymentDetails.getPaymentPolicy().getSubcost());
        pendingPayment.setCurrencyISO(paymentDetails.getPaymentPolicy().getCurrencyISO());
        return pendingPaymentRepository.saveAndFlush(pendingPayment);
    }

    private User createUser() {
        User user = new User();
        user.setUserName(Utils.getRandomUUID());
        user.setDeviceUID(Utils.getRandomUUID());
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(this.communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeCache.getIOSDeviceType());
        user.setStatus(userStatusRepository.findByName("LIMITED"));
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy, String appStroreReceipt) {
        ITunesPaymentDetails paymentDetails = new ITunesPaymentDetails(user, paymentPolicy, appStroreReceipt, 3);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.saveAndFlush(user);
        return paymentDetails;
    }

    private PaymentPolicy createPaymentPolicy(PaymentPolicyType paymentPolicyType, String appStoreProductId, Period period) {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(PaymentDetails.ITUNES_SUBSCRIPTION);
        paymentPolicy.setSubcost(BigDecimal.valueOf(1.29));
        paymentPolicy.setPeriod(period);
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setPaymentPolicyType(paymentPolicyType);
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setAppStoreProductId(appStoreProductId);
        paymentPolicy.setCommunity(communityRepository.findByRewriteUrlParameter(this.communityRewriteUrl));
        paymentPolicy.setStartDateTime(DateUtils.addDays(new Date(), -11));
        paymentPolicy.setEndDateTime(DateUtils.addDays(new Date(), 11));
        return paymentPolicyRepository.saveAndFlush(paymentPolicy);
    }
}