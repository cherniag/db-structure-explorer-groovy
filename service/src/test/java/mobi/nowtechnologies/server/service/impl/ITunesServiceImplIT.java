package mobi.nowtechnologies.server.service.impl;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentLock;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.itunes.ITunesServiceImpl;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = false)
public class ITunesServiceImplIT {

    @Resource
    ITunesServiceImpl iTunesService;
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


    @Test
    public void processITunesPaymentSuccess() throws Exception {
        final int nextSubPayment = 1523820502;
        final String nextSubPaymentTokenPart = nextSubPayment + "000";

        String transactionReceipt = "200:com.musicqubed.ios.mp.subscription.weekly.1:" + nextSubPaymentTokenPart;
        String userName = "USERNAME_1";
        String deviceUID = "DEVICE_UID_1";
        User user = createUser(userName, deviceUID, "mtv1");

        iTunesService.processInAppSubscription(user.getId(), transactionReceipt);

        User found = userRepository.findOne(user.getId());
        Assert.assertEquals(nextSubPayment, found.getNextSubPayment());
        Assert.assertTrue(found.isSubscribedStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, found.getLastSubscribedPaymentSystem());
        Assert.assertEquals(transactionReceipt, found.getBase64EncodedAppStoreReceipt());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        Assert.assertFalse(submittedPayments.isEmpty());
        Assert.assertEquals(nextSubPayment, submittedPayments.get(0).getNextSubPayment());
        Assert.assertEquals(PaymentDetailsType.FIRST, submittedPayments.get(0).getType());
        Assert.assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayments.get(0).getStatus());
        Assert.assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayments.get(0).getPaymentSystem());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertFalse(accountLogs.isEmpty());
        Assert.assertEquals(TransactionType.CARD_TOP_UP, accountLogs.get(0).getTransactionType());
    }

    @Test
    public void processITunesPaymentInCaseOfDuplicate() throws Exception {
        final int nextSubPayment = 1423820502;
        final String nextSubPaymentTokenPart = nextSubPayment + "000";

        String transactionReceipt = "200:com.musicqubed.ios.mp.subscription.weekly.1:" + nextSubPaymentTokenPart;
        String userName = "USERNAME_2";
        String deviceUID = "DEVICE_UID_2";
        User user = createUser(userName, deviceUID, "mtv1");

        iTunesPaymentLockRepository.saveAndFlush(new ITunesPaymentLock(user.getId(), nextSubPayment));

        iTunesService.processInAppSubscription(user.getId(), transactionReceipt);

        User found = userRepository.findOne(user.getId());
        Assert.assertEquals(0, found.getNextSubPayment());
        Assert.assertTrue(found.isLimited());
        Assert.assertNull(found.getLastSubscribedPaymentSystem());
        Assert.assertEquals(transactionReceipt, found.getBase64EncodedAppStoreReceipt());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        Assert.assertTrue(submittedPayments.isEmpty());

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(user.getId());
        Assert.assertTrue(accountLogs.isEmpty());
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeDao.getIOSDeviceType());
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        user = userRepository.saveAndFlush(user);
        return user;
    }
}