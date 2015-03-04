package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.service.UserNotificationService;

import java.io.UnsupportedEncodingException;

/**
 * User: gch Date: 12/19/13
 */
public class SendChargeNotificationTaskProcessor extends AbstractTaskProcessor<SendChargeNotificationTask> {

    private UserNotificationService userNotificationService;

    @Override
    public void process(SendChargeNotificationTask task) {
        LOGGER.info("About to start processing {} by {}", task, this);
        try {
            userNotificationService.sendChargeNotificationReminder(task.getUser());
        }
        catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            Community community = task.getUser().getUserGroup() != null ?
                                  task.getUser().getUserGroup().getCommunity() :
                                  null;
            String communityRewriteUrl = community != null ?
                                         community.getRewriteUrlParameter() :
                                         null;
            reScheduleTask(communityRewriteUrl, task);
        }
        LOGGER.info("Processing {} by {} done", task, this);
    }

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }
}
