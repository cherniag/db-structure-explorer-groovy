package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import static mobi.nowtechnologies.server.service.MatchUtils.getUserIdAndUserNameMatcher;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserServiceTIT {

    private static final int HOUR_SECONDS = 60 * 60;
    private static final int DAY_SECONDS = 24 * HOUR_SECONDS;
    private static final int TWO_DAY_SECONDS = 2 * DAY_SECONDS;

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserGroupRepository userGroupRepository;
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    private SubmittedPaymentRepository submittedPaymentRepository;
    @Resource
    UserStatusRepository userStatusRepository;


    @Test
    public void testCheckCredentialsAndStatus_Success() throws Exception {

        String userName = "test@test.com";
        String password = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(Calendar.getInstance().getTime());
        //String storredToken = userService.getStoredToken(userName, password);
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";

        User result = userService.checkCredentials(userName, Utils.createTimestampToken(storedToken, timestamp), timestamp, "nowtop40");

        assertEquals(1, result.getId());
    }

    @Test(expected = UserCredentialsException.class)
    public void testCheckCredentialsAndStatus_Wrong() throws Exception {

        Calendar currentDateCalendar = Calendar.getInstance();
        System.out.println(currentDateCalendar.toString());
        String userName = "66";
        String userToken = "1";
        String timestamp = "1";

        userService.checkCredentials(userName, userToken, timestamp, "CN Commercial Beta");
    }

    @Test
    public void testProceessAccountCheckCommand() {
        int userId = 1;
        User user = userService.processAccountCheckCommandForAuthorizedUser(userId);
        assertNotNull(user);
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveMigPaymentDetailsAndNotZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsNull_Success() {
        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(1);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - 100);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        testUser.setLastSubscribedPaymentSystem(null);

        userRepository.save(testUser);

        PaymentDetails currentMigPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

        currentMigPaymentDetails.setActivated(false);
        currentMigPaymentDetails.setOwner(testUser);

        paymentDetailsRepository.save(currentMigPaymentDetails);
        testUser.setCurrentPaymentDetails(currentMigPaymentDetails);
        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithInactiveMigPaymentDetailsAndZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsPSMS_Success() {
        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(0);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS - 10);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));

        userRepository.save(testUser);

        PaymentDetails currentMigPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

        currentMigPaymentDetails.setActivated(false);
        currentMigPaymentDetails.setOwner(testUser);

        paymentDetailsRepository.save(currentMigPaymentDetails);
        testUser.setCurrentPaymentDetails(currentMigPaymentDetails);
        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_FreeTrial_Success() {
        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(0);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - 10);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        testUser.setCurrentPaymentDetails(null);

        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePast_Success() {

        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(0);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        testUser.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);

        userRepository.save(testUser);

        PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        currentO2PaymentDetails.setActivated(true);
        currentO2PaymentDetails.setOwner(testUser);

        paymentDetailsRepository.save(currentO2PaymentDetails);
        testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithInActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePast_Success() {

        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(0);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        testUser.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);

        userRepository.save(testUser);

        PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        currentO2PaymentDetails.setActivated(false);
        currentO2PaymentDetails.setOwner(testUser);

        paymentDetailsRepository.save(currentO2PaymentDetails);
        testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
    }

    @Test
    public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsMIG_Success() {

        User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        testUser.setSubBalance(0);
        testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
        testUser.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        testUser.setLastSubscribedPaymentSystem(PaymentDetails.MIG_SMS_TYPE);

        userRepository.save(testUser);

        PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        currentO2PaymentDetails.setActivated(true);
        currentO2PaymentDetails.setOwner(testUser);

        paymentDetailsRepository.save(currentO2PaymentDetails);
        testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
        userRepository.save(testUser);

        List<User> users = userService.getListOfUsersForWeeklyUpdate();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    public void checkCreationNotificationTaskWhenProcessSubBalanceCommand() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        Community community = CommunityFactory.createCommunity();
        community.setRewriteUrlParameter("vf_nz");
        UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        userGroupRepository.save(userGroup);
        userRepository.save(user);
        SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
        submittedPayment.setPaymentSystem(PaymentDetails.VF_PSMS_TYPE);
        submittedPayment.setPeriod(new Period().withDuration(1).withDurationUnit(WEEKS));
        submittedPaymentRepository.save(submittedPayment);
        userService.processPaymentSubBalanceCommand(user, submittedPayment);
        List<UserTask> taskList = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(taskList.size(), is(1));
        assertThat(taskList.get(0), instanceOf(SendChargeNotificationTask.class));
        assertThat(taskList.get(0).getUser(), getUserIdAndUserNameMatcher(user));
        assertThat(taskList.get(0).getExecutionTimestamp(), greaterThan(System.currentTimeMillis()));
    }

    @Test
    public void checkCancelNotificationTaskWhenUserUnsubscribes() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        Community community = CommunityFactory.createCommunity();
        community.setRewriteUrlParameter("vf_nz");
        UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        userGroupRepository.save(userGroup);
        userRepository.save(user);
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(new Date(), user);
        sendChargeNotificationTask.setUser(user);
        taskRepository.save(sendChargeNotificationTask);
        List<UserTask> taskList = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(taskList.size(), is(1));
        userService.unsubscribeUser(user, "test");
        taskList = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(taskList.size(), is(0));
    }
}