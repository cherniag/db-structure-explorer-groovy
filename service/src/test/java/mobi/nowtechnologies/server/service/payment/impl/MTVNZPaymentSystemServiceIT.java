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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
public class MTVNZPaymentSystemServiceIT {

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
    private SMPPServiceImpl smppService;

    private BigDecimal subcost = BigDecimal.valueOf(1.29);
    private User user;
    private PaymentDetails paymentDetails;
    private PaymentPolicy paymentPolicy;

    @Before
    public void setUp() throws Exception {
        smppService = mock(SMPPServiceImpl.class);
        SMSResponse smsResponse = mock(SMSResponse.class);
        when(smsResponse.isSuccessful()).thenReturn(true);
        when(smppService.sendMessage(any(MTMessage.class))).thenReturn(smsResponse);
        smsGatewayService.setSmppService(smppService);
        taskRepository.deleteAll();
    }


    @Test
    public void startPayment() throws Exception {
        final String vFPhoneNumber = "641000000";
        final String communityRewriteUrl = "mtv1";
        user = createUser(Utils.getRandomUUID(), Utils.getRandomUUID(), communityRewriteUrl);
        paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        paymentDetails = createPaymentDetails(user, paymentPolicy, vFPhoneNumber);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.SUCCESSFUL));
        assertEquals(0, submittedPayments.size());

        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(1, pendingPayments.size());
        assertEquals(PaymentDetails.MTVNZ_PSMS_TYPE, pendingPayments.get(0).getPaymentSystem());
        assertEquals(BigDecimal.valueOf(1.29), pendingPayments.get(0).getAmount());
    }

    @Test
    public void startPaymentIfUserNotSubscriber() throws Exception {
        final String notVFPhoneNumber = "649000000";
        final String communityRewriteUrl = "mtv1";
        user = createUser(Utils.getRandomUUID(), Utils.getRandomUUID(), communityRewriteUrl);
        paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        paymentDetails = createPaymentDetails(user, paymentPolicy, notVFPhoneNumber);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        User found = userRepository.findOne(user.getId());
        assertFalse(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() < timeService.nowSeconds());
        assertNull(found.getLastSubscribedPaymentSystem());

        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertFalse(foundPaymentDetails.isActivated());
        assertEquals("User does not belong to VF", foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() > 0);

        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        assertEquals(0, pendingPayments.size());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.ERROR));
        assertEquals(0, submittedPayments.size());

        List<UserTask> userTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendUnsubscribeNotificationTask.TASK_TYPE);
        assertEquals(user.getId(), userTasks.get(0).getUser().getId());
        assertTrue(userTasks.get(0).getExecutionTimestamp() < timeService.now().getTime());
        assertTrue(userTasks.get(0).getCreationTimestamp() < timeService.now().getTime());
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
        MTVNZPSMSPaymentDetails paymentDetails = new MTVNZPSMSPaymentDetails();
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
        PaymentPolicy paymentPolicy = new PaymentPolicy();
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