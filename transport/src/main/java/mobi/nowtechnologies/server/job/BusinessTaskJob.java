package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.job.task.ProcessorContainer;
import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.service.TaskService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

/**
 * User: gch Date: 12/19/13
 */
public class BusinessTaskJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessTaskJob.class);

    private TaskService taskService;
    private ProcessorContainer processorContainer;
    private TaskExecutor executor;
    private int tasksCount = 100;
    private Set<String> supportedTaskTypes = new HashSet<>();

    public void execute() {
        try {
            LogUtils.putClassNameMDC(this.getClass());
            LOGGER.info("About to start BusinessTaskJob...");
            long now = System.currentTimeMillis();
            List<Task> tasksListForExecution = taskService.getTasksForExecution(now, supportedTaskTypes, tasksCount);
            LOGGER.info("{} tasks found, max count={}", tasksListForExecution.size(), tasksCount);
            if (tooManyTasks(tasksListForExecution.size())) {
                warnAboutTooManyTasks(now);
            }
            for (Task task : tasksListForExecution) {
                try {
                    executor.execute(new ExecutableTask(task));
                } catch (RejectedExecutionException e) {
                    LOGGER.error("Can't execute ExecutableTask({})", task.toString(), e);
                }
            }
            LOGGER.info("BusinessTaskJob completed.");
        } finally {
            LogUtils.removeClassNameMDC();
        }
    }

    private void warnAboutTooManyTasks(long now) {
        long generalCount = taskService.countTasksToExecute(now);
        LOGGER.warn("Fetched for execution {} of {} tasks. Consider increasing [tasksCount] parameter", tasksCount, generalCount);
    }

    private boolean tooManyTasks(int retrievedCount) {
        return retrievedCount >= tasksCount;
    }

    @Required
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Required
    public void setProcessorContainer(ProcessorContainer processorContainer) {
        this.processorContainer = processorContainer;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    public void setSupportedTaskTypes(Set<String> supportedTaskTypes) {
        this.supportedTaskTypes.addAll(supportedTaskTypes);
    }

    @Required
    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    private class ExecutableTask implements Runnable {

        private Task task;

        private ExecutableTask(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            processorContainer.process(task);
        }
    }
}
