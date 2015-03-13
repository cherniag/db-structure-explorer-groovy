package mobi.nowtechnologies.server.job;


import mobi.nowtechnologies.server.persistence.apptests.domain.JobTriggerRequest;
import mobi.nowtechnologies.server.persistence.apptests.repository.JobTriggerRequestRepository;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.quartz.QuartzJobBean;

public class AppTestManageJobsBean extends QuartzJobBean implements StatefulJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        final JobTriggerRequestRepository jobTriggerRequestRepository = (JobTriggerRequestRepository) context.getMergedJobDataMap().get("jobTriggerRequestRepository");
        final Scheduler scheduler = context.getScheduler();
        try {
            process(jobTriggerRequestRepository, scheduler);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void process(JobTriggerRequestRepository jobTriggerRequestService, Scheduler scheduler) {
        List<JobTriggerRequest> requests = jobTriggerRequestService.findBefore(new Date().getTime());
        logger.debug("found {} requests", requests.size());
        for (JobTriggerRequest request : requests) {
            logger.debug("processing request {}", request);
            try {
                scheduler.triggerJob(request.getJobName(), request.getJobGroupName());
            } catch (SchedulerException e) {
                logger.error("Failed to process: {}", request, e);
            }
            jobTriggerRequestService.delete(request);
        }
        logger.debug("processing done");
    }

}
