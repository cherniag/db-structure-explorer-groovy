package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendUnsubscribeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

/**
 * User: gch Date: 12/17/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class TaskServiceTestIT {

    private static final String TASK_COUNT_QUERY = "select count(*) from tb_tasks t join tb_users u on t.user_id=u.i where u.i=%s and t.taskType='%s'";
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private TaskRepository<Task> taskRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private Set<String> supportedTypes = new HashSet<>();

    private TimeService timeService = mock(TimeService.class);

    @Before
    public void setUp() throws Exception {
        supportedTypes.add("SendChargeNotificationTask");
        when(timeService.now()).thenReturn(new Date());
        taskService.setTimeService(timeService);
    }

    @Test
    public void testCreateSendChargeNotificationWithExistingCommunity() {
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        taskService.createSendChargeNotificationTask(user);
        List<UserTask> task = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(task.size(), is(1));
        assertThat(task.get(0), notNullValue());
        assertThat(task.get(0).getId(), notNullValue());
        assertThat(task.get(0).getExecutionTimestamp() - task.get(0).getCreationTimestamp(), is(2000L));
    }

    @Test
    public void testCreateSendChargeNotificationWithNonExistingCommunity() {
        Community community = createAndSaveCommunity("non-exists!");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        taskService.createSendChargeNotificationTask(user);
        int count = jdbcTemplate.queryForInt(format(TASK_COUNT_QUERY, user.getId(), SendChargeNotificationTask.TASK_TYPE));
        assertThat(count, is(0));
    }

    @Test
    public void checkIfTaskAlreadyExistsNoTasksShouldBeCreated() throws Exception {
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        taskService.createSendChargeNotificationTask(user);
        List<UserTask> task = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(task.size(), is(1));
        Long taskId = task.get(0).getId();
        long executionTimestamp = task.get(0).getExecutionTimestamp();
        taskService.createSendChargeNotificationTask(user);
        task = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        assertThat(task.size(), is(1));
        assertThat(taskId, is(task.get(0).getId()));
        assertThat(executionTimestamp, is(task.get(0).getExecutionTimestamp()));
    }

    @Test
    public void testCancelSendChargeNotificationTask() throws Exception {
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(new Date(), UserFactory.createUser(ActivationStatus.ACTIVATED));
        sendChargeNotificationTask.setUser(user);
        taskRepository.save(sendChargeNotificationTask);
        int count = jdbcTemplate.queryForInt(format(TASK_COUNT_QUERY, user.getId(), SendChargeNotificationTask.TASK_TYPE));
        assertThat(count, is(1));
        taskService.cancelSendChargeNotificationTask(user);
        count = jdbcTemplate.queryForInt(format(TASK_COUNT_QUERY, user.getId(), SendChargeNotificationTask.TASK_TYPE));
        assertThat(count, is(0));
    }

    @Test(expected = ServiceException.class)
    public void checkCancelSendChargeNotificationTaskWithUserNull() {
        taskService.cancelSendChargeNotificationTask(null);
    }

    @Test
    public void testGetTasksToExecute() throws Exception {
        List<SendChargeNotificationTask> tasksIsReady = new ArrayList<SendChargeNotificationTask>();
        List<SendChargeNotificationTask> tasksInFuture = new ArrayList<SendChargeNotificationTask>();
        long now = System.currentTimeMillis();
        for(int i = 1; i < 11; i++) {
            tasksIsReady.add(createAndStoreTask("vf_nz", now - i * 10000L, System.currentTimeMillis()));
        }
        for(int i = 1; i < 8; i++){
            tasksInFuture.add(createAndStoreTask("vf_nz", now + i * 10000L, System.currentTimeMillis()));
        }
        List<Task> taskToExecute = taskService.getTasksForExecution(now, supportedTypes, 100);
        assertThat(taskToExecute.size(), is(10));
        assertThat(taskToExecute.get(0), instanceOf(SendChargeNotificationTask.class));
        assertThat(((SendChargeNotificationTask) taskToExecute.get(0)).getUser(), notNullValue());
    }

    @Test
    public void testRescheduleTaskForCommunityWithOwnScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("vf_nz", now, now);
        taskService.reScheduleTask("vf_nz", task);
        Task saved = taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now + 2000L));
    }

    @Test
    public void testRescheduleTaskForCommunityWithoutOwnScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("samsung", now, now);
        taskService.reScheduleTask("samsung", task);
        Task saved = taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now + 4500L));
    }

    @Test
    public void testRescheduleTaskForCommunityWithEmptyScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("o2", now, now);
        taskService.reScheduleTask("o2", task);
        Task saved = taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now));
    }

    @Test
    public void createSendPaymentErrorNotificationTask() throws Exception {
        final Date creationDate = new Date();
        when(timeService.now()).thenReturn(creationDate);
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);

        taskService.createSendPaymentErrorNotificationTask(user);

        List<UserTask> tasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendPaymentErrorNotificationTask.TASK_TYPE);
        assertEquals(1, tasks.size());
        assertEquals(creationDate.getTime(), tasks.get(0).getCreationTimestamp());
        assertEquals(creationDate.getTime(), tasks.get(0).getExecutionTimestamp());
        assertTrue(tasks.get(0) instanceof SendPaymentErrorNotificationTask);
    }

    @Test
    public void createSendUnsubscribeNotificationTask() throws Exception {
        final Date creationDate = new Date();
        when(timeService.now()).thenReturn(creationDate);
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);

        taskService.createSendUnsubscribeNotificationTask(user);

        List<UserTask> tasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendUnsubscribeNotificationTask.TASK_TYPE);
        assertEquals(1, tasks.size());
        assertEquals(creationDate.getTime(), tasks.get(0).getCreationTimestamp());
        assertEquals(creationDate.getTime(), tasks.get(0).getExecutionTimestamp());
        assertTrue(tasks.get(0) instanceof SendUnsubscribeNotificationTask);
    }

    private SendChargeNotificationTask createAndStoreTask(String communityUrl, long executionTimestamp, long creationTimestamp){
        Community community = createAndSaveCommunity(communityUrl);
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(new Date(), UserFactory.createUser(ActivationStatus.ACTIVATED));
        sendChargeNotificationTask.setId(null);
        sendChargeNotificationTask.setUser(user);

        ReflectionTestUtils.setField(sendChargeNotificationTask, "executionTimestamp", executionTimestamp);
        ReflectionTestUtils.setField(sendChargeNotificationTask, "creationTimestamp", creationTimestamp);

        return taskRepository.save(sendChargeNotificationTask);
    }

    private User createAndSaveUser(UserGroup userGroup) {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(userGroup);
        return userRepository.save(user);
    }

    private UserGroup createAndSaveUserGroup(Community community) {
        UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(community);
        return userGroupRepository.save(userGroup);
    }

    private Community createAndSaveCommunity(String rewriteUrl) {
        Community community = CommunityFactory.createCommunity();
        community.setRewriteUrlParameter(rewriteUrl);
        return communityRepository.save(community);
    }
}
