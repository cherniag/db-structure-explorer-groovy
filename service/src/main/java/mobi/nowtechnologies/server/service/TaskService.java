package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.repository.TaskRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.util.Scheduler;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.List;

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

    private TaskRepository taskRepository;

    private Scheduler scheduler;

    private CommunityResourceBundleMessageSource messageSource;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSendChargeNotificationTask(User user) {
        if (user == null) {
            throw new ServiceException("Can't create SendChargeNotificationTask for user [null]");
        }
        if (messageSource.readBoolean(user.getCommunityRewriteUrl(), "sendchargenotificationtask.creation.enabled", false)) {
            SendChargeNotificationTask sendChargeNotificationTask = null;
            List<Task> existingUserTasks = taskRepository.findActiveUserTasksByUserIdAndType(user.getId(), SendChargeNotificationTask.TASK_TYPE);
            LOGGER.info("Found {} {} tasks for user id={}", isEmptyList(existingUserTasks) ?
                                                            0 :
                                                            existingUserTasks.size(), SendChargeNotificationTask.TASK_TYPE, user.getId());
            try {
                if (isEmptyList(existingUserTasks)) {
                    sendChargeNotificationTask = new SendChargeNotificationTask();
                    sendChargeNotificationTask.setCreationTimestamp(System.currentTimeMillis());
                    sendChargeNotificationTask.setUser(user);
                    sendChargeNotificationTask.setTaskStatus(TaskStatus.ACTIVE);
                    scheduler.scheduleTask(user.getCommunityRewriteUrl(), sendChargeNotificationTask);
                    taskRepository.save(sendChargeNotificationTask);
                    LOGGER.info("{} task {} has been created successfully", SendChargeNotificationTask.TASK_TYPE, sendChargeNotificationTask);
                }
            }
            catch (ServiceException e) {
                LOGGER.error(String.format("Can't create task [%s]", sendChargeNotificationTask), e);
            }
        }
        else {
            LOGGER.info("Send charge notification is not enabled for community {}", user.getCommunityRewriteUrl());
        }
    }

    private boolean isEmptyList(List<Task> existingUserTasks) {
        return existingUserTasks == null || existingUserTasks.size() == 0;
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
    public List<Task> getTasksForExecution(long executionTime, int maxTaskCount) {
        Pageable pageable = new PageRequest(0, maxTaskCount);
        return taskRepository.findTasksToExecute(executionTime, pageable);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long countTasksToExecute(long executionTime) {
        return taskRepository.countTasksToExecute(executionTime);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reScheduleTask(String communityRewriteUrl, Task task) {
        LOGGER.info("Rescheduling {}...", task);
        try {
            scheduler.reScheduleTask(communityRewriteUrl, task);
            int rowsUpdated = taskRepository.updateExecutionTimestamp(task.getId(), task.getExecutionTimestamp());
            LOGGER.info("Task {} has been rescheduled, update rows count is {}", task, rowsUpdated);
        }
        catch (ServiceException e) {
            LOGGER.error(String.format("Can't reschedule task [%s], communityRewriteUrl is %s", task, communityRewriteUrl), e);
        }
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
