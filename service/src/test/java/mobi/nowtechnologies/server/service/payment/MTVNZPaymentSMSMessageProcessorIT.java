package mobi.nowtechnologies.server.service.payment;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendUnsubscribeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
public class MTVNZPaymentSMSMessageProcessorIT {
    @Resource
    private MTVNZPaymentSMSMessageProcessor mtvnzPaymentSMSMessageProcessor;
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
    private TimeService timeService;
    @Resource
    private NZSubscriberInfoRepository nzSubscriberInfoRepository;
    @Resource
    private TaskRepository<Task> taskRepository;
    @Resource
    private AccountLogRepository accountLogRepository;

    private String communityRewriteUrl = "mtv1";
    private BigDecimal subcost = BigDecimal.valueOf(1.29);
    private PaymentPolicy paymentPolicy = new PaymentPolicy();
    private MTVNZPSMSPaymentDetails paymentDetails = new MTVNZPSMSPaymentDetails();
    private User user;

    @Test
    public void processSuccessPaymentResponse() throws Exception {
        final String msisdn = "6410000000";
        user = createUser(Utils.getRandomUUID(), Utils.getRandomUUID(), communityRewriteUrl);
        preparePendingPayment(user, "+" + msisdn);
        prepareNZSubscriberInfo(user, "+" + msisdn);
        DeliverSm deliverSm = getSuccessDeliverSm(msisdn);

        mtvnzPaymentSMSMessageProcessor.parserAndProcess(deliverSm);

        User found = userRepository.findOne(user.getId());
        assertUserWasSubscribed(found);

        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertPaymentDetailsAreStillActive(foundPaymentDetails);

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.SUCCESSFUL));
        assertSubmittedPaymentHasSuccessStatus(submittedPayments);

        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(0, pendingPayments.size());
    }

    @Test
    public void processErrorPaymentResponse() throws Exception {
        final String msisdn = "6420000000";
        user = createUser(Utils.getRandomUUID(), Utils.getRandomUUID(), communityRewriteUrl);
        preparePendingPayment(user, "+" + msisdn);
        prepareNZSubscriberInfo(user, "+" + msisdn);
        DeliverSm deliverSm = getErrorDeliverSm(msisdn);

        mtvnzPaymentSMSMessageProcessor.parserAndProcess(deliverSm);

        User found = userRepository.findOne(user.getId());
        assertUserWasNotSubscribed(found);

        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertPaymentDetailsWereDeactivated(foundPaymentDetails);

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.ERROR));
        assertSubmittedPaymentHasErrorStatus(submittedPayments);

        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(0, pendingPayments.size());

        List<UserTask> unsubscribeUserTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendUnsubscribeNotificationTask.TASK_TYPE);
        assertUserNotificationTaskIsCheduled(user, unsubscribeUserTasks);

        List<UserTask> errorUserTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendPaymentErrorNotificationTask.TASK_TYPE);
        assertUserNotificationTaskIsCheduled(user, errorUserTasks);
    }

    @After
    public void tearDown() throws Exception {
        accountLogRepository.deleteAll();
        submittedPaymentRepository.deleteAll();
        taskRepository.deleteAll();
        user.setCurrentPaymentDetails(null);
        user.setLastSuccessfulPaymentDetails(null);
        user.getPaymentDetailsList().clear();
        userRepository.save(user);
        pendingPaymentRepository.deleteAll();
        paymentDetailsRepository.delete(paymentDetails);
        paymentPolicyRepository.delete(paymentPolicy);
        userRepository.delete(user);
    }

    private void assertUserWasSubscribed(User found) {
        assertTrue(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() > timeService.nowSeconds());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, found.getLastSubscribedPaymentSystem());
    }

    private void assertPaymentDetailsAreStillActive(PaymentDetails foundPaymentDetails) {
        assertTrue(foundPaymentDetails.isActivated());
        assertTrue(foundPaymentDetails instanceof MTVNZPSMSPaymentDetails);
        assertNull(foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() == 0);
    }

    private void assertSubmittedPaymentHasSuccessStatus(List<SubmittedPayment> submittedPayments) {
        assertEquals(1, submittedPayments.size());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, submittedPayments.get(0).getPaymentSystem());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayments.get(0).getStatus());
        assertEquals(subcost, submittedPayments.get(0).getAmount());
    }

    private void assertUserWasNotSubscribed(User found) {
        assertFalse(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() < timeService.nowSeconds());
        assertNull(found.getLastSubscribedPaymentSystem());
    }

    private void assertUserNotificationTaskIsCheduled(User user, List<UserTask> unsubscribeUserTasks) {
        assertEquals(user.getId(), unsubscribeUserTasks.get(0).getUser().getId());
        assertEquals(TaskStatus.ACTIVE, unsubscribeUserTasks.get(0).getTaskStatus());
        assertTrue(unsubscribeUserTasks.get(0).getExecutionTimestamp() < timeService.now().getTime());
        assertTrue(unsubscribeUserTasks.get(0).getCreationTimestamp() < timeService.now().getTime());
    }

    private void assertSubmittedPaymentHasErrorStatus(List<SubmittedPayment> submittedPayments) {
        assertEquals(1, submittedPayments.size());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, submittedPayments.get(0).getPaymentSystem());
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayments.get(0).getStatus());
        assertEquals(subcost, submittedPayments.get(0).getAmount());
    }

    private void assertPaymentDetailsWereDeactivated(PaymentDetails foundPaymentDetails) {
        assertFalse(foundPaymentDetails.isActivated());
        assertTrue(foundPaymentDetails instanceof MTVNZPSMSPaymentDetails);
        assertEquals("UNDELIV", foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() > 0);
    }

    private void prepareNZSubscriberInfo(User user, String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = new NZSubscriberInfo(msisdn);
        nzSubscriberInfo.setPayIndicator("Prepay");
        nzSubscriberInfo.setProviderName("Unknown Operator");
        nzSubscriberInfo.setBillingAccountNumber("300001121");
        nzSubscriberInfo.setBillingAccountName("Simplepostpay_CCRoam");
        nzSubscriberInfo.setUserId(user.getId());
        nzSubscriberInfoRepository.saveAndFlush(nzSubscriberInfo);
    }

    private void preparePendingPayment(User user, String phoneNumber) {
        PaymentPolicy paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy, phoneNumber);
        createPendingPayment(user, paymentDetails);
    }

    private DeliverSm getSuccessDeliverSm(String msisdn) throws InvalidDeliveryReceiptException {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn(msisdn);
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.ACCEPTD);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        return deliverSm;
    }

    private DeliverSm getErrorDeliverSm(String msisdn) throws InvalidDeliveryReceiptException {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn(msisdn);
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.UNDELIV);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        return deliverSm;
    }

    private PendingPayment createPendingPayment(User user, PaymentDetails paymentDetails){
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setType(PaymentDetailsType.FIRST);
        pendingPayment.setPaymentSystem(PaymentDetails.MTVNZ_PSMS_TYPE);
        pendingPayment.setPeriod(paymentDetails.getPaymentPolicy().getPeriod());
        pendingPayment.setAmount(paymentDetails.getPaymentPolicy().getSubcost());
        pendingPayment.setCurrencyISO(paymentDetails.getPaymentPolicy().getCurrencyISO());
        return pendingPaymentRepository.saveAndFlush(pendingPayment);
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setNextSubPayment(timeService.nowSeconds() - 10);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy, String phoneNumber){
        paymentDetails.setActivated(true);
        paymentDetails.setOwner(user);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setPhoneNumber(phoneNumber);
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.AWAITING);
        paymentDetails.resetMadeAttemptsForFirstPayment();
        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.saveAndFlush(user);
        return paymentDetails;
    }

    private PaymentPolicy createPaymentPolicy(String communityRewriteUrl, PaymentPolicyType paymentPolicyType){
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(PaymentDetails.MTVNZ_PSMS_TYPE);
        paymentPolicy.setSubcost(subcost);
        paymentPolicy.setPeriod(new Period(WEEKS, 1));
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setShortCode("2150");
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setPaymentPolicyType(paymentPolicyType);
        paymentPolicy.setCommunity(communityRepository.findByRewriteUrlParameter(communityRewriteUrl));
        return paymentPolicyRepository.saveAndFlush(paymentPolicy);
    }
}