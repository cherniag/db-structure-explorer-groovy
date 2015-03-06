package mobi.nowtechnologies.server.persistence.domain.task;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
@Entity
@DiscriminatorValue(SendUnsubscribeNotificationTask.TASK_TYPE)
public class SendUnsubscribeNotificationTask extends UserTask {
    public static final String TASK_TYPE = "SendUnsubscribeNotificationTask";
}
