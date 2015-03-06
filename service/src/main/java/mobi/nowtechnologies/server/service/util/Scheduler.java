package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.server.persistence.domain.task.Task;

/**
 * User: gch Date: 12/17/13
 */
public interface Scheduler {

    void scheduleTask(Task task, String communityRewriteUrl);
}
