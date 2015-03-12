package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.service.UserNotificationService;

import javax.annotation.Resource;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public class SimpleUserNotificationTaskProcessor implements TaskProcessor<UserTask> {
    @Resource
    NZSubscriberInfoRepository nzSubscriberInfoRepository;
    @Resource
    UserNotificationService userNotificationService;
    @Resource
    TaskService taskService;


    private String messageKey;
    private String supportedTaskType;

    @Override
    public void process(UserTask task) {
        logger().info("Start processing task {}", task);
        User user = task.getUser();
        NZSubscriberInfo nzSubscriberInfo = nzSubscriberInfoRepository.findSubscriberInfoByUserId(user.getId());
        if(nzSubscriberInfo == null){
            logger().warn("Could not find NZSubscriberInfo for user {}, SMS can not be sent", user.getId());
            taskService.removeTask(task);
            return;
        }

        try {
            userNotificationService.sendSMSByKey(user, nzSubscriberInfo.getMsisdn(), messageKey);
        } catch (Exception e) {
            logger().error("Could not send SMS to msisdn " + nzSubscriberInfo.getMsisdn() + ", message: " + e.getMessage(), e);
        } finally {
            logger().info("Remove task {}", task);
            taskService.removeTask(task);
        }
    }

    @Override
    public boolean supports(Task task) {
        return supportedTaskType.equals(task.getTaskType());
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setSupportedTaskType(String supportedTaskType) {
        this.supportedTaskType = Preconditions.checkNotNull(supportedTaskType);
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
