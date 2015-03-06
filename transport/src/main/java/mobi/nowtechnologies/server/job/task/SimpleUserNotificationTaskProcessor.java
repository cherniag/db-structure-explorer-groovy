package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public class SimpleUserNotificationTaskProcessor extends AbstractUserNotificationTaskProcessor {
    private NZSubscriberInfoRepository nzSubscriberInfoRepository;
    private String messageKey;
    private Class<?> supportedTaskClass;

    @Override
    public void process(UserTask task) {
        LOGGER.info("Start processing task {}", task);
        User user = task.getUser();
        NZSubscriberInfo nzSubscriberInfo = nzSubscriberInfoRepository.findSubscriberInfoByUserId(user.getId());
        if(nzSubscriberInfo == null){
            LOGGER.warn("Could not find NZSubscriberInfo for user {}, SMS can not be sent", user.getId());
            getTaskService().removeTask(task);
            return;
        }

        try {
            getUserNotificationService().sendSMSByKey(user, nzSubscriberInfo.getMsisdn(), messageKey);
        } catch (Exception e) {
            LOGGER.error("Could not send SMS to msisdn " + nzSubscriberInfo.getMsisdn() + ", message: " + e.getMessage(), e);
        } finally {
            LOGGER.info("Remove task {}", task);
            getTaskService().removeTask(task);
        }
    }

    @Override
    public boolean supports(Task task) {
        return task.getClass().isAssignableFrom(supportedTaskClass);
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setNzSubscriberInfoRepository(NZSubscriberInfoRepository nzSubscriberInfoRepository) {
        this.nzSubscriberInfoRepository = nzSubscriberInfoRepository;
    }

    public void setSupportedTaskClass(Class<?> supportedTaskClass) {
        this.supportedTaskClass = supportedTaskClass;
    }
}
