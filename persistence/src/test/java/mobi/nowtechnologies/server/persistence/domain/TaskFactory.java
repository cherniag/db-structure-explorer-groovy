package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import static java.lang.System.currentTimeMillis;

/**
 * User: gch Date: 12/17/13
 */
public class TaskFactory {

    public static SendChargeNotificationTask createSendChargeNotificationTask() {
        SendChargeNotificationTask sendChargeNotificationTask = new SendChargeNotificationTask();
        sendChargeNotificationTask.setExecutionTimestamp(currentTimeMillis() + 1000L);
        sendChargeNotificationTask.setCreationTimestamp(currentTimeMillis());
        sendChargeNotificationTask.setId(Long.valueOf(10));
        sendChargeNotificationTask.setTaskStatus(TaskStatus.ACTIVE);
        sendChargeNotificationTask.setUser(UserFactory.createUser(ActivationStatus.ACTIVATED));
        return sendChargeNotificationTask;
    }
}
