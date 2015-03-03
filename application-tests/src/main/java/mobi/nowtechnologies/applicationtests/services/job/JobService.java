package mobi.nowtechnologies.applicationtests.services.job;


import mobi.nowtechnologies.server.persistence.apptests.domain.JobTriggerRequest;
import mobi.nowtechnologies.server.persistence.apptests.repository.JobTriggerRequestRepository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class JobService {

    public static final String PENDING_PAYMENT_JOB = "paymentJob";
    public static final String DEFAULT_GROUP_NAME = "DEFAULT";
    @Resource
    JobTriggerRequestRepository jobTriggerRequestRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void startPaymentJob() {
        logger.info("start payment job");
        JobTriggerRequest jobTriggerRequest = new JobTriggerRequest(PENDING_PAYMENT_JOB, DEFAULT_GROUP_NAME);
        logger.info("about to save new trigger request {}", jobTriggerRequest);
        jobTriggerRequestRepository.saveAndFlush(jobTriggerRequest);
    }
}
