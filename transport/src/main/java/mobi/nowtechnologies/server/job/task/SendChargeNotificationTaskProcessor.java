package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;

import java.io.UnsupportedEncodingException;

/**
 * User: gch Date: 12/19/13
 */
public class SendChargeNotificationTaskProcessor extends AbstractUserNotificationTaskProcessor {

    @Override
    public void process(UserTask task) {
        LOGGER.info("About to start processing {} by {}", task, this);
        try {
            getUserNotificationService().sendChargeNotificationReminder(task.getUser());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not send SMS to user " + task.getUser().getUserName() + ", message: " + e.getMessage(), e);
        } finally {
            String communityRewriteUrl = task.getUser().getCommunityRewriteUrl();
            LOGGER.info("Rescheduling task {} with community {}", task, communityRewriteUrl);
            getTaskService().reScheduleTask(communityRewriteUrl, task);
        }
        LOGGER.info("Processing {} by {} done", task, this);
    }

    @Override
    public boolean supports(Task task) {
        return task instanceof SendChargeNotificationTask;
    }
}
