package mobi.nowtechnologies.server.persistence.domain.task;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
@Entity
@DiscriminatorValue(SendPaymentErrorNotificationTask.TASK_TYPE)
public class SendPaymentErrorNotificationTask extends UserTask {
    public static final String TASK_TYPE = "SendPaymentErrorNotificationTask";

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
