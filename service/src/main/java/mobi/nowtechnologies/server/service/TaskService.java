package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendPaymentErrorNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.SendUnsubscribeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
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

    private CommunityResourceBundleMessageSource messageSource;

    private TimeService timeService;

    @Transactional
    public void createSendChargeNotificationTask(User user) {
        final String communityRewriteUrl = user.getCommunityRewriteUrl();

        if (!messageSource.readBoolean(communityRewriteUrl, "sendchargenotificationtask.creation.enabled", false)) {
            LOGGER.info("Send charge notification is not enabled for community {}", communityRewriteUrl);
            return;
        }

        List<UserTask> existingUserTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
        if (!CollectionUtils.isEmpty(existingUserTasks)) {
            LOGGER.info("Found {} {} tasks for user id={}", existingUserTasks.size(), SendChargeNotificationTask.TASK_TYPE, user.getId());
            return;
        }

        try {
            Date serverTime = timeService.now();

            SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask(serverTime, user);

            long executeInterval = getNextExecInterval(communityRewriteUrl, sendChargeNotificationTask);

            sendChargeNotificationTask.scheduleAfter(executeInterval);

            taskRepository.save(sendChargeNotificationTask);

            LOGGER.info("Task {} has been created successfully", sendChargeNotificationTask);
        } catch (Exception e) {
            LOGGER.error("Can't create SendChargeNotificationTask for user " + user.getId(), e);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendPaymentErrorNotificationTask(User user) {
        Date serverTime = timeService.now();

        UserTask userTask = new SendPaymentErrorNotificationTask(serverTime, user);

        LOGGER.info("Create {} for user {}", userTask.getClass().getSimpleName(), user.getId());

        taskRepository.save(userTask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendUnsubscribeNotificationTask(User user) {
        Date serverTime = timeService.now();

        UserTask userTask = new SendUnsubscribeNotificationTask(serverTime, user);

        LOGGER.info("Create {} for user {}", userTask.getClass().getSimpleName(), user.getId());

        taskRepository.save(userTask);
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
        Page<Task> tasksToExecute = taskRepository.findTasksToExecute(executionTime, supportedTypes, pageable);
        if(tasksToExecute.hasNextPage()) {
            LOGGER.warn("Fetched for execution {} tasks, but total count is {}", tasksToExecute.getNumberOfElements(), tasksToExecute.getTotalElements());
        }
        return tasksToExecute.getContent();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reScheduleTask(String communityRewriteUrl, Task task) {
        Task existing = taskRepository.findOne(task.getId());

        LOGGER.info("Rescheduling {}...", existing);

        try {
            long executeInterval = getNextExecInterval(communityRewriteUrl, existing);

            existing.scheduleAfter(executeInterval);

            LOGGER.info("Task {} has been rescheduled with executionTimestamp {}", existing, existing.getExecutionTimestamp());
        } catch (Exception e) {
            LOGGER.error(String.format("Can't reschedule task [%s], communityRewriteUrl is %s", existing, communityRewriteUrl), e);
        }
    }

    private long getNextExecInterval(String communityRewriteUrl, Task task) {
        String messageCode = task.getTaskType().toLowerCase() + ".schedule.period.in.millis";
        String nextExecution = messageSource.getMessage(communityRewriteUrl, messageCode, null, null);
        return Long.valueOf(nextExecution);
    }

    public void setTaskRepository(TaskRepository<Task> taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
