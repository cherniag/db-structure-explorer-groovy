package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.Task;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public interface TaskProcessor<T extends Task> {

    void process(T task);

    boolean supports(Task task);

}
