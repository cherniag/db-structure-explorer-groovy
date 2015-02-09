package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: gch
 * Date: 12/17/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
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
    private TaskRepository taskRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateSendChargeNotificationWithExistingCommunity() {
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        taskService.createSendChargeNotificationTask(user);
        List<Task> task = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
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

    @Test(expected = ServiceException.class)
    public void checkCreateSendChargeNotificationWithUserNull() {
        taskService.createSendChargeNotificationTask(null);
    }

    @Test
    public void checkIfTaskAlreadyExistsNoTasksShouldBeCreated() throws Exception {
        Community community = createAndSaveCommunity("vf_nz");
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        taskService.createSendChargeNotificationTask(user);
        List<Task> task = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
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
        SendChargeNotificationTask sendChargeNotificationTask = TaskFactory.createSendChargeNotificationTask();
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
        for(int i = 1; i < 11; i++){
            tasksIsReady.add(createAndStoreTask("vf_nz",now - i * 10000L));
        }
        for(int i = 1; i < 8; i++){
            tasksInFuture.add(createAndStoreTask("vf_nz",now + i * 10000L));
        }
        List<Task> taskToExecute = taskService.getTasksForExecution(now, 100);
        assertThat(taskToExecute.size(), is(10));
        assertThat(taskToExecute.get(0), instanceOf(SendChargeNotificationTask.class));
        assertThat(((SendChargeNotificationTask)taskToExecute.get(0)).getUser(), notNullValue());
    }

    @Test
    public void testRescheduleTaskForCommunityWithOwnScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("vf_nz", now);
        taskService.reScheduleTask("vf_nz", task);
        Task saved = (Task) taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now + 2000L));
    }

    @Test
    public void testRescheduleTaskForCommunityWithoutOwnScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("samsung", now);
        taskService.reScheduleTask("samsung", task);
        Task saved = (Task) taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now + 4500L));
    }

    @Test
    public void testRescheduleTaskForCommunityWithEmptyScheduleProperty() throws Exception {
        long now = System.currentTimeMillis();
        SendChargeNotificationTask task = createAndStoreTask("o2", now);
        taskService.reScheduleTask("o2", task);
        Task saved = (Task) taskRepository.findOne(task.getId());
        assertThat(saved, notNullValue());
        assertThat(saved.getExecutionTimestamp(), is(now));
    }

    @Test
    public void testCountTasksToExecute() throws Exception {
        long executeAt = System.currentTimeMillis() - 1000L;
        createAndStoreTask("o2", executeAt);
        createAndStoreTask("o2", executeAt);
        createAndStoreTask("o2", executeAt);
        createAndStoreTask("o2", executeAt);
        createAndStoreTask("o2", executeAt);
        long count = taskService.countTasksToExecute(System.currentTimeMillis());
        assertThat(count, is(5L));
    }

    private SendChargeNotificationTask createAndStoreTask(String communityUrl, long executionTimestamp){
        Community community = createAndSaveCommunity(communityUrl);
        UserGroup userGroup = createAndSaveUserGroup(community);
        User user = createAndSaveUser(userGroup);
        SendChargeNotificationTask sendChargeNotificationTask = TaskFactory.createSendChargeNotificationTask();
        sendChargeNotificationTask.setId(null);
        sendChargeNotificationTask.setUser(user);
        sendChargeNotificationTask.setExecutionTimestamp(executionTimestamp);
        return (SendChargeNotificationTask) taskRepository.save(sendChargeNotificationTask);
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
