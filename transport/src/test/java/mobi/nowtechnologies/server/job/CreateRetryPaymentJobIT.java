package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.job.executor.PendingPaymentExecutor;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.aop.SMSNotification;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.*;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/17/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(locations = {
                "classpath:transport-root-test.xml", "classpath:jobs-test.xml"}),
        @ContextConfiguration(locations = {
                "classpath:transport-servlet-test.xml"})})
public class CreateRetryPaymentJobIT {

    @Resource
    private CreateRetryPaymentJob createRetryPaymentJob;

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
    private SubmitedPaymentRepository submitedPaymentRepository;

    @Mock
    private PendingPaymentExecutor pendingPaymentExecutorMocked;

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
        submitedPaymentRepository.deleteAll();
        user.setCurrentPaymentDetails(null);
        user.getPaymentDetailsList().clear();
        userRepository.save(user);
        paymentDetailsRepository.delete(paymentDetails);
        userRepository.delete(user);
    }

    @Test
    public void checkO2PaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new O2PSMSPaymentDetails());

        createRetryPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(ERROR, submittedPayment.getStatus());
        assertEquals(O2_PSMS_TYPE, submittedPayment.getPaymentSystem());
        assertEquals("TaskRejectedException", submittedPayment.getDescriptionError());
    }

    @Test
    public void checkVFPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+6419411945", "deviceUID1", "vf_nz", new VFPSMSPaymentDetails());

        createRetryPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(ERROR, submittedPayment.getStatus());
        assertEquals(VF_PSMS_TYPE, submittedPayment.getPaymentSystem());
        assertEquals("TaskRejectedException", submittedPayment.getDescriptionError());
    }

    @Test
    public void checkPayPalPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new PayPalPaymentDetails());

        createRetryPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(ERROR, submittedPayment.getStatus());
        assertEquals(PAYPAL_TYPE, submittedPayment.getPaymentSystem());
        assertEquals("TaskRejectedException", submittedPayment.getDescriptionError());
    }

    @Test
    public void checkSagePayPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new SagePayCreditCardPaymentDetails());

        createRetryPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals(ERROR, submittedPayment.getStatus());
        assertEquals(SAGEPAY_CREDITCARD_TYPE, submittedPayment.getPaymentSystem());
        assertEquals("TaskRejectedException", submittedPayment.getDescriptionError());
    }

    @Test
    public void checkMigPaymentSystemCommitsErrorResponse() throws Exception {
        user = prepareUserForPayment("+44719411945", "deviceUID1", "o2", new MigPaymentDetails());

        createRetryPaymentJob.execute();

        SubmittedPayment submittedPayment = getSubmittedPayment(user);
        assertEquals("TaskRejectedException", submittedPayment.getDescriptionError());
        assertEquals(ERROR, submittedPayment.getStatus());
        assertEquals(MIG_SMS_TYPE, submittedPayment.getPaymentSystem());
    }

    private SubmittedPayment getSubmittedPayment(User user1) {
        return submitedPaymentRepository.findByTypeAndUserIdOrderByTimestampDesc(PaymentDetailsType.FIRST, user1.getId());
    }

    private void mockPaymentExecutor() {
        createRetryPaymentJob.setExecutor(pendingPaymentExecutorMocked);
        doThrow(TaskRejectedException.class).when(pendingPaymentExecutorMocked).execute(any(PendingPayment.class));
    }

    private void disablePaymentsForExistingUsers() {
        for (User user : userRepository.findAll()) {
            user.setNextSubPayment(Integer.MAX_VALUE);
            userRepository.save(user);
        }
        submitedPaymentRepository.deleteAll();
    }

    private void mockUserNotificationService() throws UnsupportedEncodingException {
        when(userNotificationServiceMocked.sendPaymentFailSMS(any(PaymentDetails.class))).thenReturn(true);
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
        details.setLastPaymentStatus(ERROR);
        details.setRetriesOnError(1);
        details.setPaymentPolicy(paymentPolicyRepository.findOne(228));
        return paymentDetailsRepository.saveAndFlush(details);
    }
}
