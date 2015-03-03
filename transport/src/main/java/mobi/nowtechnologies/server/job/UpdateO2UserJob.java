package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.service.HazelcastService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.service.HazelcastService.QUEUE_O2_USERS_FOR_UPDATE;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import org.springframework.scheduling.quartz.QuartzJobBean;

public class UpdateO2UserJob extends QuartzJobBean implements StatefulJob {

    private transient static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserJob.class);
    private transient static int POOL_SIZE;

    private transient CommunityResourceBundleMessageSource messageSource;
    private transient ExecutorService executor;
    private transient BlockingQueue<List<Integer>> usersIdQueue;

    public UpdateO2UserJob() {
        messageSource = (CommunityResourceBundleMessageSource) SpringContext.getBean("serviceMessageSource");
        readProperties();
        HazelcastInstance hz = new HazelcastService().getHazelcastInstance();
        usersIdQueue = hz.getQueue(QUEUE_O2_USERS_FOR_UPDATE);
        executor = Executors.newFixedThreadPool(POOL_SIZE);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("starting ... POOL_SIZE: [{}] queue size: [{}] ", POOL_SIZE, usersIdQueue.size());
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                submitTaskForExecution();
            }

        }
        catch (Throwable t) {
            LOG.error("Job ended with error.", t);
        }
        finally {

            LOG.info("finished, cleaning up executor!");
            try {
                executor.shutdown();
                executor.awaitTermination(2, TimeUnit.HOURS);
            }
            catch (Exception ex) {
                LOG.error("Error while awiting termination:" + ex, ex);
            }
            LOG.info("job completed!");
        }
    }

    private void submitTaskForExecution() {
        List<Integer> usersId = usersIdQueue.poll();
        if (isNotEmpty(usersId)) {
            executor.submit(new UpdateO2UserBatchTask(usersId));
        }

    }

    private void readProperties() {
        POOL_SIZE = messageSource.readInt("job.update.o2.pool.size", 2);
    }

}
