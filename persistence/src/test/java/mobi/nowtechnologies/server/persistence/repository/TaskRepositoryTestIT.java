package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static java.lang.System.currentTimeMillis;

import com.google.common.collect.Iterables;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: gch Date: 12/17/13
 */

public class TaskRepositoryTestIT extends AbstractRepositoryIT {

    public static final String SEND_CHARGE_NOTIFICATION_TASK_NAME = SendChargeNotificationTask.class.getSimpleName();
    public static final String WRONG_TASK_NAME = "WRONG";
    public static final int WRONG_USER_ID = 1500;
    @Resource
    private TaskRepository taskRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private SendChargeNotificationTaskRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    private Set<String> supportedTypes = new HashSet<>();

    @Before
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAllInBatch();

        supportedTypes.add("SendChargeNotificationTask");
    }

    @Test
    public void checkSendChargeNotificationTaskIsSavedSuccessfully() {
        SendChargeNotificationTask task = new SendChargeNotificationTask(new Date(), UserFactory.createUser(ActivationStatus.ACTIVATED));
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
        List<Task> taskList = taskRepository.findTasksToExecute(currentTimeMillis(), supportedTypes, pageable);
        assertThat(taskList.size(), is(2));
        assertThat(taskList, hasItem(task1));
        assertThat(taskList, hasItem(task2));
        assertThat(taskList, not(hasItem(task3)));
    }

    @Test
    @Ignore
    public void checkFetchingActiveTasksForExecutionWithLimit() {
        User user1 = createAndSaveUser();
        for (int i = 0; i < 20; i++) {
            createAndSaveSendChargeNotificationTask(user1, currentTimeMillis() - 1L);
        }
        Pageable pageable = new PageRequest(0, 8);
        List<Task> taskList = taskRepository.findTasksToExecute(currentTimeMillis(), supportedTypes, pageable);
        assertThat(taskList.size(), is(8));
        pageable = new PageRequest(0, 30);
        taskList = taskRepository.findTasksToExecute(currentTimeMillis(), supportedTypes, pageable);
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
        SendChargeNotificationTask savedTask = (SendChargeNotificationTask) taskRepository.findOne(taskId);
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
        SendChargeNotificationTask task = new SendChargeNotificationTask(new Date(), user);
        ReflectionTestUtils.setField(task, "executionTimestamp", executionTimestamp);
        return (SendChargeNotificationTask) taskRepository.save(task);
    }


}
