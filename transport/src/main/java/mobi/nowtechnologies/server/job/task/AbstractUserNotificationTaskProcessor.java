package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.shared.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: gch Date: 12/19/13
 */
public abstract class AbstractUserNotificationTaskProcessor implements TaskProcessor<UserTask> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private TaskService taskService;
    private UserNotificationService userNotificationService;

    protected void reScheduleTask(String communityRewriteUrl, Task task) {
        LOGGER.info("Rescheduling task {} with community {}", task, communityRewriteUrl);
        taskService.reScheduleTask(communityRewriteUrl, task);
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public UserNotificationService getUserNotificationService() {
        return userNotificationService;
    }

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }
}
