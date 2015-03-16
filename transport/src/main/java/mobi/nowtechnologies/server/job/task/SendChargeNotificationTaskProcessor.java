package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.service.UserNotificationService;

import javax.annotation.Resource;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: gch Date: 12/19/13
 */
public class SendChargeNotificationTaskProcessor implements TaskProcessor<UserTask> {
    @Resource
    UserNotificationService userNotificationService;
    @Resource
    TaskService taskService;

    @Override
    public void process(UserTask task) {
        logger().info("About to start processing {} by {}", task, this);
        try {
            userNotificationService.sendChargeNotificationReminder(task.getUser());
        } catch (UnsupportedEncodingException e) {
            logger().error("Could not send SMS to user " + task.getUser().getUserName() + ", message: " + e.getMessage(), e);
        } finally {
            String communityRewriteUrl = task.getUser().getCommunityRewriteUrl();
            logger().info("Rescheduling task {} with community {}", task, communityRewriteUrl);
            taskService.reScheduleTask(communityRewriteUrl, task);
        }
        logger().info("Processing {} by {} done", task, this);
    }

    @Override
    public boolean supports(Task task) {
        return task instanceof SendChargeNotificationTask;
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
