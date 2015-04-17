package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.job.executor.PendingPaymentExecutor;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.aop.SMSNotification;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.MIG_SMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.PAYPAL_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.SAGEPAY_CREDITCARD_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.VF_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;

import javax.annotation.Resource;

import java.io.UnsupportedEncodingException;

import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.AsyncResult;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Author: Gennadii Cherniaiev Date: 4/17/2014
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({@ContextConfiguration(locations = {"classpath:transport-root-test.xml"}), @ContextConfiguration(locations = {"classpath:transport-servlet-test.xml"})})
public class CreatePendingPaymentJobHandleExceptionIT {

    private static final String UNKNOWN_EXCEPTION = "Unknown exception";
    @Resource
    private CreatePendingPaymentJob createPendingPaymentJob;

    @Resource(name = "smsNotificationAspect")
    private SMSNotification smsNotification;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource
    private PaymentPolicyRepository paymentPolicyRepository;

    @Resource
    private SubmittedPaymentRepository submittedPaymentRepository;

    @Resource
    private PendingPaymentExecutor pendingPaymentExecutor;

    @Mock
    private UserNotificationService userNotificationServiceMocked;
    private User user;
    private PaymentDetails paymentDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockPaymentExecutor();
        mockUserNotificationService();
        disablePaymentsForExistingUsers();
    }

    @After
    public void tearDown() throws Exception {
        submittedPaymentRepository.deleteAll();
        user.setCurrentPaymentDetails(null);
        userRepository.save(user);
        paymentDetailsRepository.delete(paymentDetails);
        userRepository.delete(user);
    }

    @Test
    public void checkO2PaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new O2PSMSPaymentDetails());

        createPendingPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(O2_PSMS_TYPE, submittedPayment.getPaymentSystem());
        assertEquals(UNKNOWN_EXCEPTION, submittedPayment.getDescriptionError());
    }

    @Test
    public void checkVFPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+6419411945", "deviceUID1", "vf_nz", new VFPSMSPaymentDetails());

        createPendingPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(VF_PSMS_TYPE, submittedPayment.getPaymentSystem());
        assertEquals(UNKNOWN_EXCEPTION, submittedPayment.getDescriptionError());
    }

    @Test
    public void checkPayPalPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new PayPalPaymentDetails());

        createPendingPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(PAYPAL_TYPE, submittedPayment.getPaymentSystem());
        assertEquals(UNKNOWN_EXCEPTION, submittedPayment.getDescriptionError());
    }

    @Test
    public void checkSagePayPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new SagePayCreditCardPaymentDetails());

        createPendingPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(SAGEPAY_CREDITCARD_TYPE, submittedPayment.getPaymentSystem());
        assertEquals(UNKNOWN_EXCEPTION, submittedPayment.getDescriptionError());
    }

    @Test
    public void checkMigPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new MigPaymentDetails());

        createPendingPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(UNKNOWN_EXCEPTION, submittedPayment.getDescriptionError());
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(MIG_SMS_TYPE, submittedPayment.getPaymentSystem());
    }

    private SubmittedPayment getSubmittedPayment(User user1) {
        return submittedPaymentRepository.findByTypeAndUserIdOrderByTimestampDesc(PaymentDetailsType.FIRST, user1.getId());
    }

    private void mockPaymentExecutor() {
        TaskExecutor taskExecutor = mock(TaskExecutor.class);
        doThrow(TaskRejectedException.class).when(taskExecutor).execute(any(Runnable.class));
        pendingPaymentExecutor.setExecutor(taskExecutor);
        createPendingPaymentJob.setExecutor(pendingPaymentExecutor);
    }

    private void disablePaymentsForExistingUsers() {
        for (User user : userRepository.findAll()) {
            user.setNextSubPayment(Integer.MAX_VALUE);
            userRepository.save(user);
        }
    }

    private void mockUserNotificationService() throws UnsupportedEncodingException {
        when(userNotificationServiceMocked.sendPaymentFailSMS(any(PaymentDetails.class))).thenReturn(new AsyncResult<>(true));
        smsNotification.setUserNotificationService(userNotificationServiceMocked);
    }

    private User prepareUserForPayment(String userName, String deviceUID, String communityRewriteUrl, PaymentDetails paymentDetails) {
        User user = createAndSaveUser(userName, deviceUID, communityRewriteUrl);
        this.paymentDetails = createAndSavePaymentDetails(user, paymentDetails);
        user.setCurrentPaymentDetails(this.paymentDetails);
        return userRepository.saveAndFlush(user);
    }

    private User createAndSaveUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setUserName(userName);
        user.setActivationStatus(ACTIVATED);
        user.setMobile(userName);
        user.setDeviceUID(deviceUID);
        user.setUserGroup(userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl));
        DeviceType deviceType = new DeviceType();
        deviceType.setI((byte) 2);
        user.setDeviceType(deviceType);
        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 10);
        user.setStatus(userStatus);
        user.setProvider(ProviderType.O2);
        user.setSegment(SegmentType.CONSUMER);
        user.setLastDeviceLogin(Utils.getEpochSeconds() - 1000);
        user.setNextSubPayment(Utils.getEpochSeconds() - 1000);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    private PaymentDetails createAndSavePaymentDetails(User user, PaymentDetails details) {
        details.setActivated(true);
        details.setOwner(user);
        details.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        details.setPaymentPolicy(paymentPolicyRepository.findOne(228));
        return paymentDetailsRepository.saveAndFlush(details);
    }
}
