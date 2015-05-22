package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.HazelcastService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.service.HazelcastService.QUEUE_O2_USERS_FOR_UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.Lists;
import com.hazelcast.core.HazelcastInstance;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.quartz.QuartzJobBean;

public class ForkO2UsersForUpdateJob extends QuartzJobBean implements StatefulJob {

    private static final Logger LOG = LoggerFactory.getLogger(ForkO2UsersForUpdateJob.class);
    private static int BATCH_SIZE;
    private static int PERIOD;

    private transient UserRepository userRepository;
    private transient UserGroupRepository userGroupRepository;
    private transient CommunityResourceBundleMessageSource messageSource;
    private transient BlockingQueue<List<Integer>> userIdsQueue;

    public ForkO2UsersForUpdateJob() {
        messageSource = (CommunityResourceBundleMessageSource) SpringContext.getBean("serviceMessageSource");
        userRepository = (UserRepository) SpringContext.getBean("userRepository");
        userGroupRepository = (UserGroupRepository) SpringContext.getBean("userGroupRepository");
        readProperties();
        HazelcastInstance hz = new HazelcastService().getHazelcastInstance();
        userIdsQueue = hz.getQueue(QUEUE_O2_USERS_FOR_UPDATE);
    }


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            forkUsers();
        } catch (Throwable t) {
            LOG.error("ForkO2UsersForUpdateJob error:", t);
        }
    }

    public void forkUsers() {
        LOG.info("fork O2 user starts [{}]", userIdsQueue.size());

        if (!userIdsQueue.isEmpty()) {
            LOG.info("queue is not empty, skipping");
            return;
        }

        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl(O2_COMMUNITY_REWRITE_URL);

        List<Integer> users = selectUsersForUpdate(o2UserGroup.getId());

        LOG.info("found [{}] users, batch size [{}]", users.size(), BATCH_SIZE);
        List<List<Integer>> partitions = Lists.partition(users, BATCH_SIZE);
        for (List<Integer> p : partitions) {
            userIdsQueue.offer(new ArrayList<Integer>(p));
        }
        LOG.info("fork O2 user completed, offered [{}] partitions", partitions.size());
    }

    public List<Integer> selectUsersForUpdate(int userGroupId) {
        return userRepository.findUsersForUpdate(getTimeBeforeWhichUsersWasNotUpdated(), userGroupId);
    }

    private long getTimeBeforeWhichUsersWasNotUpdated() {
        return new DateTime().minusHours(PERIOD).getMillis();
    }

    private void readProperties() {
        PERIOD = messageSource.readInt("job.update.o2.users.period.hours", 24);
        BATCH_SIZE = messageSource.readInt("job.update.o2.users.bach.size", 50);
    }

}
