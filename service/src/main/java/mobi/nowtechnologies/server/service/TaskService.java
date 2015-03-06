package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendUnsubscribeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.util.IntervalScheduler;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: gch Date: 12/17/13
 */
public class TaskService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private TaskRepository<Task> taskRepository;

    private IntervalScheduler scheduler;

    private CommunityResourceBundleMessageSource messageSource;

    private TimeService timeService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendChargeNotificationTask(User user) {
        if (!messageSource.readBoolean(user.getCommunityRewriteUrl(), "sendchargenotificationtask.creation.enabled", false)) {
            LOGGER.info("Send charge notification is not enabled for community {}", user.getCommunityRewriteUrl());
            return;
        }

        List<UserTask> existingUserTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        if (!CollectionUtils.isEmpty(existingUserTasks)) {
            LOGGER.info("Found {} {} tasks for user id={}", existingUserTasks.size(), SendChargeNotificationTask.TASK_TYPE, user.getId());
            return;
        }

        try {
            SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask();
            fillUserTask(sendChargeNotificationTask, user);
            scheduler.scheduleTask(sendChargeNotificationTask, user.getCommunityRewriteUrl());
            taskRepository.save(sendChargeNotificationTask);
            LOGGER.info("Task {} has been created successfully", sendChargeNotificationTask);
        } catch (Exception e) {
            LOGGER.error("Can't create SendChargeNotificationTask for user " + user.getId(), e);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendPaymentErrorNotificationTask(User user) {
        createInternal(new SendPaymentErrorNotificationTask(), user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendUnsubscribeNotificationTask(User user) {
        createInternal(new SendUnsubscribeNotificationTask(), user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelSendChargeNotificationTask(User user) {
        if (user == null) {
            throw new ServiceException("Can't create SendChargeNotificationTask for user [null]");
        }
        LOGGER.info("Canceling {} for user id={}", SendChargeNotificationTask.TASK_TYPE, user.getId());
        int rowsDeleted = taskRepository.deleteByUserIdAndTaskType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        LOGGER.info("{} task(s) of user id={} has been deleted", rowsDeleted, user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Task> getTasksForExecution(long executionTime, Collection<String> supportedTypes, int maxTaskCount) {
        Pageable pageable = new PageRequest(0, maxTaskCount);
        return taskRepository.findTasksToExecute(executionTime, supportedTypes, pageable);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long countTasksToExecute(long executionTime) {
        return taskRepository.countTasksToExecute(executionTime);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reScheduleTask(String communityRewriteUrl, Task task) {
        LOGGER.info("Rescheduling {}...", task);
        try {
            scheduler.scheduleTask(task, communityRewriteUrl);
            int rowsUpdated = taskRepository.updateExecutionTimestamp(task.getId(), task.getExecutionTimestamp());
            LOGGER.info("Task {} has been rescheduled with executionTimestamp {}, update rows count is {}", task, task.getExecutionTimestamp(), rowsUpdated);
        } catch (Exception e) {
            LOGGER.error(String.format("Can't reschedule task [%s], communityRewriteUrl is %s", task, communityRewriteUrl), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeTask(Task task){
        taskRepository.delete(task);
    }

    private void createInternal(UserTask userTask, User user){
        LOGGER.info("Create {} for user {}", userTask.getClass().getSimpleName(), user.getId());
        fillUserTask(userTask, user);
        taskRepository.save(userTask);
        LOGGER.info("Task {} has been created successfully", userTask);
    }

    private void fillUserTask(UserTask userTask, User user) {
        long currentTimeMillis = timeService.now().getTime();
        userTask.setCreationTimestamp(currentTimeMillis);
        userTask.setExecutionTimestamp(currentTimeMillis);
        userTask.setUser(user);
        userTask.setTaskStatus(TaskStatus.ACTIVE);
    }

    public void setTaskRepository(TaskRepository<Task> taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setScheduler(IntervalScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
