package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

/**
 * User: gch Date: 12/17/13
 */
public class Scheduler {

    private static final String SCHEDULE_PERIOD_IN_MILLIS_PROPERTY = ".schedule.period.in.millis";
    private CommunityResourceBundleMessageSource messageSource;

    public void scheduleTask(String communityRewriteUrl, Task task) {
        long executeInterval = getExecutionInterval(communityRewriteUrl, task);
        long taskCreationTimestamp = task.getCreationTimestamp() != 0 ?
                                     task.getCreationTimestamp() :
                                     System.currentTimeMillis();
        task.setExecutionTimestamp(taskCreationTimestamp + executeInterval);
    }

    public void reScheduleTask(String communityRewriteUrl, Task task) {
        long executeInterval = getExecutionInterval(communityRewriteUrl, task);
        task.setExecutionTimestamp(task.getExecutionTimestamp() + executeInterval);
    }

    private long getExecutionInterval(String communityRewriteUrl, Task task) {
        String messageCode = task.getClass().getSimpleName().toLowerCase() + SCHEDULE_PERIOD_IN_MILLIS_PROPERTY;
        String nextExecution = messageSource.getMessage(communityRewriteUrl, messageCode, null, null);
        long executeInterval = 0;
        try {
            executeInterval = Long.valueOf(nextExecution);
        } catch (NumberFormatException e) {
            throw new ServiceException(String.format("Invalid number format [%s] for property [%s] - can't schedule task [%s]", nextExecution, messageCode, task));
        }
        return executeInterval;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
