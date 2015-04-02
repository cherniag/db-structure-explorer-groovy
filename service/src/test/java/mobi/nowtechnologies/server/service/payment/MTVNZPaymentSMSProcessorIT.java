package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.util.PhoneData;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.device.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.MTVNZPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendUnsubscribeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
public class MTVNZPaymentSMSProcessorIT {
    @Resource
    private MTVNZPaymentSMSProcessor mtvnzPaymentSMSProcessor;
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
    private TaskRepository<Task> taskRepository;
    @Resource
    private AccountLogRepository accountLogRepository;

    private String communityRewriteUrl = "mtvnz";
    private BigDecimal subcost = BigDecimal.valueOf(1.29);
    private PaymentPolicy paymentPolicy;
    private MTVNZPSMSPaymentDetails paymentDetails;
    private User user;

    @Test
    public void processSuccessPaymentResponse() throws Exception {
        final String mobile = "+6410000000";
        final DeliverSm deliverSm = getSuccessDeliverSm(mobile);
        createUser(mobile);
        preparePendingPayment(mobile);

        mtvnzPaymentSMSProcessor.parserAndProcess(deliverSm);

        pendingPaymentIsAbsent();
        userGotSubscription();
        paymentDetailsAreStillActive();
        submittedPaymentHasStatus(SUCCESSFUL);
    }

    @Test
    public void processErrorPaymentResponse() throws Exception {
        final String mobile = "+6420000000";
        final DeliverSm deliverSm = getErrorDeliverSm(mobile);
        createUser(mobile);
        preparePendingPayment(mobile);

        mtvnzPaymentSMSProcessor.parserAndProcess(deliverSm);

        pendingPaymentIsAbsent();
        userHasNotGotSubscription();
        paymentDetailsWereDeactivated();
        submittedPaymentHasStatus(ERROR);
        notificationTaskIsScheduled(SendUnsubscribeNotificationTask.TASK_TYPE);
        notificationTaskIsScheduled(SendPaymentErrorNotificationTask.TASK_TYPE);
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

    private void pendingPaymentIsAbsent() {
        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(0, pendingPayments.size());
    }

    private void userGotSubscription() {
        User found = userRepository.findOne(user.getId());
        assertTrue(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() > timeService.nowSeconds());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, found.getLastSubscribedPaymentSystem());
    }

    private void paymentDetailsAreStillActive() {
        User found = userRepository.findOne(user.getId());
        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(foundPaymentDetails.isActivated());
        assertTrue(foundPaymentDetails instanceof MTVNZPSMSPaymentDetails);
        assertNull(foundPaymentDetails.getDescriptionError());
        assertEquals(SUCCESSFUL, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() == 0);
    }

    private void userHasNotGotSubscription() {
        User found = userRepository.findOne(user.getId());
        assertFalse(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() < timeService.nowSeconds());
        assertNull(found.getLastSubscribedPaymentSystem());
    }

    private void notificationTaskIsScheduled(String taskType) {
        List<UserTask> tasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), taskType);
        assertEquals(user.getId(), tasks.get(0).getUser().getId());
        assertTrue(tasks.get(0).getExecutionTimestamp() < timeService.now().getTime());
        assertTrue(tasks.get(0).getCreationTimestamp() < timeService.now().getTime());
    }

    private void submittedPaymentHasStatus(PaymentDetailsStatus error) {
        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(1, submittedPayments.size());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, submittedPayments.get(0).getPaymentSystem());
        assertEquals(error, submittedPayments.get(0).getStatus());
        assertEquals(subcost, submittedPayments.get(0).getAmount());
    }

    private void paymentDetailsWereDeactivated() {
        User found = userRepository.findOne(user.getId());
        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertFalse(foundPaymentDetails.isActivated());
        assertTrue(foundPaymentDetails instanceof MTVNZPSMSPaymentDetails);
        assertEquals("UNDELIV", foundPaymentDetails.getDescriptionError());
        assertEquals(ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() > 0);
    }

    private DeliverSm getSuccessDeliverSm(String msisdn) throws InvalidDeliveryReceiptException {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn(new PhoneData(msisdn).getData());
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.ACCEPTD);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        return deliverSm;
    }

    private DeliverSm getErrorDeliverSm(String msisdn) throws InvalidDeliveryReceiptException {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn(new PhoneData(msisdn).getData());
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.UNDELIV);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        return deliverSm;
    }

    private void preparePendingPayment(String phoneNumber) {
        PaymentPolicy paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy, phoneNumber);
        createPendingPayment(user, paymentDetails);
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

    private User createUser(String mobile) {
        user = new User();
        user.setDeviceUID(Utils.getRandomUUID());
        user.setUserName(Utils.getRandomUUID());
        user.setMobile(mobile);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setNextSubPayment(timeService.nowSeconds() - 10);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        return userRepository.saveAndFlush(user);
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy, String phoneNumber){
        paymentDetails = new MTVNZPSMSPaymentDetails();
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
        paymentPolicy = new PaymentPolicy();
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