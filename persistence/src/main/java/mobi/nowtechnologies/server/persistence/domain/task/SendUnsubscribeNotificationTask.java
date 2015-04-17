package mobi.nowtechnologies.server.persistence.domain.task;

import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.Date;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
@Entity
@DiscriminatorValue(SendUnsubscribeNotificationTask.TASK_TYPE)
public class SendUnsubscribeNotificationTask extends UserTask {
    public static final String TASK_TYPE = "SendUnsubscribeNotificationTask";

    protected SendUnsubscribeNotificationTask() {
    }

    public SendUnsubscribeNotificationTask(Date serverTime, User user) {
        super(serverTime, user);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
