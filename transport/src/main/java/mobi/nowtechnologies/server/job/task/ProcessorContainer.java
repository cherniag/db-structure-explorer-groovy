package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.shared.Processor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: gch Date: 12/16/13
 */
public class ProcessorContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorContainer.class);

    private Map<String, Processor<Task>> processorMap;

    public void process(Task task) {
        Processor<Task> processor = processorMap.get(task.getClass().getSimpleName());
        if (processor != null) {
            processor.process(task);
        }
        else {
            LOGGER.error("There is no corresponding processor for task {}", task);
        }
    }

    public void setProcessorMap(Map<String, Processor<Task>> processorMap) {
        this.processorMap = processorMap;
    }
}
