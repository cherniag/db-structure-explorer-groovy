package mobi.nowtechnologies.server.persistence.domain.task;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: gch
 * Date: 12/16/13
 */
@Entity
@DiscriminatorValue(SendChargeNotificationTask.TASK_TYPE)
public class SendChargeNotificationTask extends UserTask {
    public static final String TASK_TYPE = "SendChargeNotificationTask";

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }

}
