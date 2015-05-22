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
@DiscriminatorValue(SendPaymentErrorNotificationTask.TASK_TYPE)
public class SendPaymentErrorNotificationTask extends UserTask {
    public static final String TASK_TYPE = "SendPaymentErrorNotificationTask";

    protected SendPaymentErrorNotificationTask() {
    }

    public SendPaymentErrorNotificationTask(Date serverTime, User user) {
        super(serverTime, user);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
