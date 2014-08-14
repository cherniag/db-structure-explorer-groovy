package mobi.nowtechnologies.server.persistence.repository;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.TaskFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * User: gch
 * Date: 12/17/13
 */
@Ignore
public class TaskRepositoryTestIT extends AbstractRepositoryIT {
    public static final String SEND_CHARGE_NOTIFICATION_TASK_NAME = SendChargeNotificationTask.class.getSimpleName();
    public static final String WRONG_TASK_NAME = "WRONG";
    public static final int WRONG_USER_ID = 1500;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private DrmRepository drmRepository;

    @Resource
    private SendChargeNotificationTaskRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() {
        taskRepository.deleteAll();
        drmRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    public void checkSendChargeNotificationTaskIsSavedSuccessfully() {
        SendChargeNotificationTask task = TaskFactory.createSendChargeNotificationTask();
        task.setId(null);
        task.setUser(userRepository.findOne(110));
        repository.save(task);
        List<SendChargeNotificationTask> savedTasks = repository.findAll();
        assertThat(savedTasks.size(), is(1));
        Task actual = Iterables.getFirst(savedTasks, null);
        assertThat(actual, instanceOf(SendChargeNotificationTask.class));
        assertThat(actual.getId(), is(task.getId()));
        assertThat(actual.getExecutionTimestamp(), is(task.getExecutionTimestamp()));
        assertThat(actual.getCreationTimestamp(), is(task.getCreationTimestamp()));
        assertThat(actual.getTaskStatus(), is(task.getTaskStatus()));
    }


    @Test
    public void testFindActiveUserTasksByUserIdAndTaskType() throws Exception {
        User user1 = createAndSaveUser();
        SendChargeNotificationTask task1 = createAndSaveSendChargeNotificationTask(user1);
        SendChargeNotificationTask task2 = createAndSaveSendChargeNotificationTask(user1);
        User user2 = createAndSaveUser();
        SendChargeNotificationTask task3 = createAndSaveSendChargeNotificationTask(user2);
        List<SendChargeNotificationTask> saved = taskRepository.findActiveUserTasksByUserIdAndType(user1.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(2));
        assertThat(saved.get(0).getId(), is(task1.getId()));
        assertThat(saved.get(1).getId(), is(task2.getId()));
        saved = taskRepository.findActiveUserTasksByUserIdAndType(user2.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(1));
        assertThat(saved.get(0).getId(), is(task3.getId()));
        saved = taskRepository.findActiveUserTasksByUserIdAndType(WRONG_USER_ID, SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(0));
        saved = taskRepository.findActiveUserTasksByUserIdAndType(user1.getId(), WRONG_TASK_NAME);
        assertThat(saved.size(), is(0));
    }

    @Test
    public void checkRemovingTaskByUserIdAndTaskType() throws Exception {
        User user = createAndSaveUser();
        createAndSaveSendChargeNotificationTask(user);
        taskRepository.deleteByUserIdAndTaskType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        List<SendChargeNotificationTask> saved = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(0));
        User savedUser = userRepository.findOne(user.getId());
        assertThat(savedUser, notNullValue());
        assertThat(savedUser.getId(), is(savedUser.getId()));
    }

    @Test
    public void checkRemovingTaskByUserIdAndWrongTaskType() throws Exception {
        User user = createAndSaveUser();
        SendChargeNotificationTask task = createAndSaveSendChargeNotificationTask(user);
        taskRepository.deleteByUserIdAndTaskType(user.getId(), WRONG_TASK_NAME);
        List<SendChargeNotificationTask> saved = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(1));
        assertThat(saved.get(0).getId(), is(task.getId()));
    }

    @Test
    public void checkRemovingTaskByWrongUserIdAndTaskType() throws Exception {
        User user = createAndSaveUser();
        SendChargeNotificationTask task = createAndSaveSendChargeNotificationTask(user);
        taskRepository.deleteByUserIdAndTaskType(WRONG_USER_ID, SEND_CHARGE_NOTIFICATION_TASK_NAME);
        List<SendChargeNotificationTask> saved = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(1));
        assertThat(saved.get(0).getId(), is(task.getId()));
        taskRepository.deleteByUserIdAndTaskType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        saved = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SEND_CHARGE_NOTIFICATION_TASK_NAME);
        assertThat(saved.size(), is(0));
    }

    @Test
    public void checkFetchingActiveTasksForExecution() {
        User user1 = createAndSaveUser();
        SendChargeNotificationTask task1 = createAndSaveSendChargeNotificationTask(user1, currentTimeMillis() - 1L);
        User user2 = createAndSaveUser();
        SendChargeNotificationTask task2 = createAndSaveSendChargeNotificationTask(user2, currentTimeMillis() - 1L);
        SendChargeNotificationTask task3 = createAndSaveSendChargeNotificationTask(user2, currentTimeMillis() + 100 * 1000L);
        long totalCount = taskRepository.count();
        assertThat(totalCount, is(3l));
        Pageable pageable = new PageRequest(0, 5);
        List<Task> taskList = taskRepository.findTasksToExecute(currentTimeMillis(), pageable);
        assertThat(taskList.size(), is(2));
        assertThat(taskList, hasItem(task1));
        assertThat(taskList, hasItem(task2));
        assertThat(taskList, not(hasItem(task3)));
    }

    @Test
    public void checkFetchingActiveTasksForExecutionWithLimit() {
        User user1 = createAndSaveUser();
        for (int i = 0; i < 20; i++) {
            createAndSaveSendChargeNotificationTask(user1, currentTimeMillis() - 1L);
        }
        long totalCount = taskRepository.count();
        assertThat(totalCount, is(20l));
        Pageable pageable = new PageRequest(0, 8);
        List<Task> taskList = taskRepository.findTasksToExecute(currentTimeMillis(), pageable);
        assertThat(taskList.size(), is(8));
        pageable = new PageRequest(0, 30);
        taskList = taskRepository.findTasksToExecute(currentTimeMillis(), pageable);
        assertThat(taskList.size(), is(20));
    }

    @Test
    public void checkTaskIsSavedCorrectly() {
        long executionTimestamp = currentTimeMillis();
        assertEquals(taskRepository.count(), 0);
        createAndSaveSendChargeNotificationTask(null, executionTimestamp);
        Long taskId = repository.findAll().get(0).getId();
        long newExecutionTimestamp = executionTimestamp + 1000L;
        taskRepository.updateExecutionTimestamp(taskId, newExecutionTimestamp);
        entityManager.clear();
        SendChargeNotificationTask savedTask = (SendChargeNotificationTask)taskRepository.findOne(taskId);
        assertEquals(newExecutionTimestamp, savedTask.getExecutionTimestamp());
    }

    @Test
    public void checkTaskCountToExecute() throws Exception {
        long executionTimestamp = currentTimeMillis() - 1000L;
        createAndSaveSendChargeNotificationTask(null, executionTimestamp);
        createAndSaveSendChargeNotificationTask(null, executionTimestamp);
        long count = taskRepository.countTasksToExecute(currentTimeMillis());
        assertThat(count, is(2L));
        createAndSaveSendChargeNotificationTask(null, executionTimestamp);
        count = taskRepository.countTasksToExecute(currentTimeMillis());
        assertThat(count, is(3L));

    }

    private User createAndSaveUser() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserName(UUID.randomUUID().toString());
        user.setDeviceUID(user.getUserName());
        return userRepository.save(user);
    }

    private SendChargeNotificationTask createAndSaveSendChargeNotificationTask(User user) {
        return createAndSaveSendChargeNotificationTask(user, currentTimeMillis());
    }

    private SendChargeNotificationTask createAndSaveSendChargeNotificationTask(User user, Long executionTimestamp) {
        SendChargeNotificationTask task = TaskFactory.createSendChargeNotificationTask();
        task.setUser(user);
        task.setExecutionTimestamp(executionTimestamp);
        return (SendChargeNotificationTask) taskRepository.save(task);
    }


}
