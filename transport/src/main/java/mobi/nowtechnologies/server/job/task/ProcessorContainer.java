package mobi.nowtechnologies.server.job.task;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.shared.Processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: gch Date: 12/16/13
 */
public class ProcessorContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorContainer.class);

    private List<TaskProcessor> processors;

    public void process(Task task) {
        for (TaskProcessor processor : processors) {
            if(processor.supports(task)){
                LOGGER.debug("Processor {} is chosen", processor);
                processor.process(task);
                return;
            }
        }
        LOGGER.error("There is no corresponding processor for task {}", task);
    }

    public void setProcessors(List<TaskProcessor> processors) {
        this.processors = processors;
    }
}
