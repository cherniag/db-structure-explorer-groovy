package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentLock;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.persistence.repository.ITunesPaymentLockRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import javax.annotation.Resource;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class ITunesServiceIT {

    @Resource
    ITunesService iTunesService;
    @Resource
    UserGroupRepository userGroupRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    ITunesPaymentLockRepository iTunesPaymentLockRepository;
    @Resource
    SubmittedPaymentRepository submittedPaymentRepository;
    @Resource
    AccountLogRepository accountLogRepository;
    @Resource
    UserStatusRepository userStatusRepository;

    @After
    public void tearDown() throws Exception {
        accountLogRepository.deleteAll();
        submittedPaymentRepository.deleteAll();
    }

    @Test
    public void processITunesPaymentSuccess() throws Exception {
        final int nextSubPayment = 1523820502;
        final String productId = "com.musicqubed.o2.autorenew.test";
        final String transactionId = "555555555555";
        String transactionReceipt = createAutoRenewableToken(productId, transactionId, nextSubPayment);

        User user = createUser("o2");

        iTunesService.processInAppSubscription(user, transactionReceipt);

        User found = userRepository.findOne(user.getId());
        Assert.assertEquals(nextSubPayment, found.getNextSubPayment());
        Assert.assertTrue(found.isSubscribedStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, found.getLastSubscribedPaymentSystem());
        Assert.assertEquals(transactionReceipt, found.getBase64EncodedAppStoreReceipt());
        Assert.assertEquals(transactionId, found.getAppStoreOriginalTransactionId());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        Assert.assertFalse(submittedPayments.isEmpty());
        Assert.assertEquals(nextSubPayment, submittedPayments.get(0).getNextSubPayment());
        Assert.assertEquals(PaymentDetailsType.FIRST, submittedPayments.get(0).getType());
        Assert.assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayments.get(0).getStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayments.get(0).getPaymentSystem());
        Assert.assertEquals(transactionId, submittedPayments.get(0).getAppStoreOriginalTransactionId());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertFalse(accountLogs.isEmpty());
        Assert.assertEquals(TransactionType.CARD_TOP_UP, accountLogs.get(0).getTransactionType());
    }

    @Test
    public void processITunesUpSellSuccess() throws Exception {
        final int purchaseTime = Utils.getEpochSeconds() - 1000;
        final String productId = "com.musicqubed.o2.autorenew.test";
        final String transactionId = "555555555555";
        String transactionReceipt = createOneTimePaymentToken(productId, transactionId, purchaseTime);

        User user = createUser("o2");

        iTunesService.processInAppSubscription(user, transactionReceipt);

        User found = userRepository.findOne(user.getId());
        int expectedNextSubPayment = new Period(DurationUnit.MONTHS, 1).toNextSubPaymentSeconds(purchaseTime);
        Assert.assertEquals(expectedNextSubPayment, found.getNextSubPayment());
        Assert.assertTrue(found.isSubscribedStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, found.getLastSubscribedPaymentSystem());
        Assert.assertEquals(transactionReceipt, found.getBase64EncodedAppStoreReceipt());
        Assert.assertEquals(transactionId, found.getAppStoreOriginalTransactionId());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        Assert.assertFalse(submittedPayments.isEmpty());
        Assert.assertEquals(expectedNextSubPayment, submittedPayments.get(0).getNextSubPayment());
        Assert.assertEquals(PaymentDetailsType.FIRST, submittedPayments.get(0).getType());
        Assert.assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayments.get(0).getStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayments.get(0).getPaymentSystem());
        Assert.assertEquals(transactionId, submittedPayments.get(0).getAppStoreOriginalTransactionId());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertFalse(accountLogs.isEmpty());
        Assert.assertEquals(TransactionType.CARD_TOP_UP, accountLogs.get(0).getTransactionType());
    }

    @Test
    public void processITunesPaymentInCaseOfDuplicate() throws Exception {
        final int nextSubPayment = 1581586902;
        final String productId = "com.musicqubed.o2.autorenew.test";
        final String transactionId = "555555555555";
        String transactionReceipt = createAutoRenewableToken(productId, transactionId, nextSubPayment);

        User user = createUser("o2");

        iTunesPaymentLockRepository.saveAndFlush(new ITunesPaymentLock(user.getId(), nextSubPayment));

        iTunesService.processInAppSubscription(user, transactionReceipt);

        User found = userRepository.findOne(user.getId());
        Assert.assertEquals(0, found.getNextSubPayment());
        Assert.assertTrue(found.isLimited());
        Assert.assertNull(found.getLastSubscribedPaymentSystem());
        Assert.assertNull(found.getBase64EncodedAppStoreReceipt());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        Assert.assertTrue(submittedPayments.isEmpty());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertTrue(accountLogs.isEmpty());
    }

    private String createAutoRenewableToken(String productId, String transactionId, int nextSubPayment) {
        return String.format("renewable:200:0:%s:%s:%s000", productId, transactionId, nextSubPayment);
    }

    private String createOneTimePaymentToken(String productId, String transactionId, int purchaseDate) {
        return String.format("onetime:200:0:%s:%s:%s000", productId, transactionId, purchaseDate);
    }

    private User createUser(String communityRewriteUrl) {
        User user = new User();
        user.setUserName(Utils.getRandomUUID());
        user.setDeviceUID(Utils.getRandomUUID());
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeCache.getIOSDeviceType());
        user.setStatus(userStatusRepository.findByName(UserStatusType.LIMITED.name()));
        user = userRepository.saveAndFlush(user);
        return user;
    }
}