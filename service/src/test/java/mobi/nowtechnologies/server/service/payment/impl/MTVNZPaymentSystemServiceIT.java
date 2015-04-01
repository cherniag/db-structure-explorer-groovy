package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
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
import mobi.nowtechnologies.server.service.payment.MTVNZPaymentSystemService;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import com.sentaca.spring.smpp.mt.MTMessage;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
public class MTVNZPaymentSystemServiceIT {
    private static final String SHORT_CODE = "2150";
    private static final String COMMUNITY_REWRITE_URL = "mtv1";

    @Resource(name = "mtvnzPaymentSystemService")
    private MTVNZPaymentSystemService mtvnzPaymentSystemService;
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
    @Resource(name = "vf_nz.service.VFNZSMSGatewayService")
    private VFNZSMSGatewayServiceImpl smsGatewayService;

    private ArgumentCaptor<MTMessage> messageArgumentCaptor = ArgumentCaptor.forClass(MTMessage.class);

    private BigDecimal subcost = BigDecimal.valueOf(1.29);
    private User user;
    private MTVNZPSMSPaymentDetails paymentDetails;
    private PaymentPolicy paymentPolicy;

    @Before
    public void setUp() throws Exception {
        SMPPServiceImpl smppService = mock(SMPPServiceImpl.class);
        SMSResponse smsResponse = mock(SMSResponse.class);
        when(smsResponse.isSuccessful()).thenReturn(true);
        when(smppService.sendMessage(messageArgumentCaptor.capture())).thenReturn(smsResponse);
        smsGatewayService.setSmppService(smppService);

        taskRepository.deleteAll();
    }


    @Test
    public void startPaymentSuccess() throws Exception {
        final String validVFPhoneNumber = "+641000000";
        PendingPayment pendingPayment = createPendingPayment(validVFPhoneNumber);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        paymentSMSWasSentToProvider(validVFPhoneNumber);
        userHasNotPaid(); // async vf response has not been received yet
        noSubmittedPaymentsForUser();
        pendingPaymentExists(); // until vf response received

    }

    @Test
    public void startPaymentIfUserDoesNotBelongToVF() throws Exception {
        final String notBelongToVFPhoneNumber = "+649000000";
        PendingPayment pendingPayment = createPendingPayment(notBelongToVFPhoneNumber);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        userHasNotPaid();
        paymentDetailsWereDisabled("User does not belong to VF");
        noPendingPaymentsForUser();
        noSubmittedPaymentsForUser();
        sendUnsubscribeNotificationTaskIsSheduled();
    }

    @Test
    public void startPaymentIfMSISDNNotFound() throws Exception {
        final String notFoundInVFPhoneNumber = "+380939000000";
        PendingPayment pendingPayment = createPendingPayment(notFoundInVFPhoneNumber);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        userHasNotPaid();
        paymentDetailsWereDisabled("MSISDN not found");
        noPendingPaymentsForUser();
        noSubmittedPaymentsForUser();
        sendUnsubscribeNotificationTaskIsSheduled();
    }

    @Test
    public void startPaymentIfProviderConnectionProblem() throws Exception {
        final String connectionProblemVFPhoneNumber = "+648000000";
        PendingPayment pendingPayment = createPendingPayment(connectionProblemVFPhoneNumber);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        userHasNotPaid();
        paymentDetailsWithErrorStatusButStillActivatedWithoutIncrement();
        noPendingPaymentsForUser();
        submittedPaymentExists();
        noSendUnsubscribeNotificationTaskForUser();
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

    private PendingPayment createPendingPayment(String validVFPhoneNumber) {
        createUser();
        createPaymentPolicy();
        createPaymentDetails(validVFPhoneNumber);
        return createPendingPayment();
    }

    private void paymentSMSWasSentToProvider(String msisdn) {
        MTMessage sentMessage = messageArgumentCaptor.getValue();
        assertEquals(SHORT_CODE, sentMessage.getOriginatingAddress());
        assertEquals(msisdn, sentMessage.getDestinationAddress());
        assertEquals("Your payment for your weekly MTV Trax subscription was successful. You were charged $" + subcost + ". To unsubscribe, text STOP to 2150", sentMessage.getContent());
    }


    private void submittedPaymentExists() {
        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(1, submittedPayments.size());
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayments.get(0).getStatus());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, submittedPayments.get(0).getPaymentSystem());
        assertEquals(subcost, submittedPayments.get(0).getAmount());
    }

    private void paymentDetailsWithErrorStatusButStillActivatedWithoutIncrement() {
        User found = userRepository.findOne(user.getId());
        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(foundPaymentDetails.isActivated());
        assertEquals("Unexpected http status code [503] so the madeRetries won't be incremented", foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertEquals(0, foundPaymentDetails.getDisableTimestampMillis());
        assertEquals(0, foundPaymentDetails.getMadeAttempts());
        assertEquals(-1, foundPaymentDetails.getMadeRetries());
    }

    private void noSendUnsubscribeNotificationTaskForUser() {
        List<UserTask> userTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendUnsubscribeNotificationTask.TASK_TYPE);
        assertTrue(userTasks.isEmpty());
    }

    private User userHasNotPaid() {
        User found = userRepository.findOne(user.getId());
        assertFalse(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() < timeService.nowSeconds());
        assertNull(found.getLastSubscribedPaymentSystem());
        return found;
    }

    private void noPendingPaymentsForUser() {
        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(0, pendingPayments.size());
    }

    private void sendUnsubscribeNotificationTaskIsSheduled() {
        List<UserTask> userTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendUnsubscribeNotificationTask.TASK_TYPE);
        assertEquals(user.getId(), userTasks.get(0).getUser().getId());
        assertTrue(userTasks.get(0).getExecutionTimestamp() < timeService.now().getTime());
        assertTrue(userTasks.get(0).getCreationTimestamp() < timeService.now().getTime());
    }

    private void paymentDetailsWereDisabled(String reason) {
        User found = userRepository.findOne(user.getId());
        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertFalse(foundPaymentDetails.isActivated());
        assertEquals(reason, foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() > 0);
    }

    private void pendingPaymentExists() {
        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(1, pendingPayments.size());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, pendingPayments.get(0).getPaymentSystem());
        assertEquals(BigDecimal.valueOf(1.29), pendingPayments.get(0).getAmount());
    }

    private void noSubmittedPaymentsForUser() {
        List<SubmittedPayment>  submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.values()));
        assertEquals(0, submittedPayments.size());
    }

    private PendingPayment createPendingPayment(){
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setType(PaymentDetailsType.FIRST);
        pendingPayment.setPaymentSystem(PaymentDetails.MTVNZ_PSMS_TYPE);
        pendingPayment.setPeriod(paymentDetails.getPaymentPolicy().getPeriod());
        pendingPayment.setAmount(paymentDetails.getPaymentPolicy().getSubcost());
        pendingPayment.setCurrencyISO(paymentDetails.getPaymentPolicy().getCurrencyISO());
        return pendingPaymentRepository.save(pendingPayment);
    }

    private void createUser() {
        user = new User();
        user.setUserName(Utils.getRandomUUID());
        user.setDeviceUID(Utils.getRandomUUID());
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(COMMUNITY_REWRITE_URL);
        user.setUserGroup(userGroup);
        user.setNextSubPayment(timeService.nowSeconds() - 10);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        userRepository.save(user);
    }

    private void createPaymentDetails(String phoneNumber){
        paymentDetails = new MTVNZPSMSPaymentDetails();
        paymentDetails.setActivated(true);
        paymentDetails.setOwner(user);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setPhoneNumber(phoneNumber);
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.AWAITING);
        paymentDetails.resetMadeAttemptsForFirstPayment();
        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.save(user);
    }

    private void createPaymentPolicy(){
        paymentPolicy = new PaymentPolicy();
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(PaymentDetails.MTVNZ_PSMS_TYPE);
        paymentPolicy.setSubcost(subcost);
        paymentPolicy.setPeriod(new Period(WEEKS, 1));
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setShortCode(SHORT_CODE);
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.RECURRENT);
        paymentPolicy.setCommunity(communityRepository.findByRewriteUrlParameter(COMMUNITY_REWRITE_URL));
        paymentPolicyRepository.save(paymentPolicy);
    }
}