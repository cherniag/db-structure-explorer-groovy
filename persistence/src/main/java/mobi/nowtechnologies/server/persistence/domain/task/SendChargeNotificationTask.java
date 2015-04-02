package mobi.nowtechnologies.server.persistence.domain.task;

import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: gch Date: 12/16/13
 */
@Entity
@DiscriminatorValue(SendChargeNotificationTask.TASK_TYPE)
public class SendChargeNotificationTask extends UserTask {

    public static final String TASK_TYPE = "SendChargeNotificationTask";

    protected SendChargeNotificationTask() {
    }

    public SendChargeNotificationTask(Date serverTime, User user) {
        super(serverTime, user);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).toString();
    }

}
