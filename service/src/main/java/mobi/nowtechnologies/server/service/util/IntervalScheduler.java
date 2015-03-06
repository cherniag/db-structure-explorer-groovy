package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

/**
 * User: gch Date: 12/17/13
 */
public class IntervalScheduler implements Scheduler {

    private static final String SCHEDULE_PERIOD_IN_MILLIS_PROPERTY = ".schedule.period.in.millis";
    private CommunityResourceBundleMessageSource messageSource;

    @Override
    public void scheduleTask(Task task, String communityRewriteUrl) {
        long executeInterval = getExecutionInterval(communityRewriteUrl, task);
        long latest = Math.max(task.getCreationTimestamp(), task.getExecutionTimestamp());
        task.setExecutionTimestamp(latest + executeInterval);
    }

    private long getExecutionInterval(String communityRewriteUrl, Task task) {
        String messageCode = task.getClass().getSimpleName().toLowerCase() + SCHEDULE_PERIOD_IN_MILLIS_PROPERTY;
        String nextExecution = messageSource.getMessage(communityRewriteUrl, messageCode, null, null);
        return Long.valueOf(nextExecution);
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
