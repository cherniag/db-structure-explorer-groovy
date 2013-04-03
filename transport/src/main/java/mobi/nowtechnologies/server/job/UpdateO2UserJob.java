package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.o2.soa.subscriberdata.SubscriberProfileType;
import uk.co.o2.soa.subscriberservice.GetSubscriberProfileFault;
import uk.co.o2.soa.subscriberservice.SubscriberService;
import uk.co.o2.soa.utils.SOAPLoggingHandler;
import uk.co.o2.soa.utils.SecurityHandler;
import uk.co.o2.soa.utils.SubscriberPortDecorator;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public class UpdateO2UserJob extends QuartzJobBean implements StatefulJob {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserJob.class);

    private transient UserRepository userRepository;
    private transient UpdateO2UserTask task;

    public UpdateO2UserJob() {
        userRepository = (UserRepository) SpringContext.getBean("userRepository");
        task = (UpdateO2UserTask) SpringContext.getBean("job.UpdateO2UserTask");
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("starting ...");
        try {

            List<User> users = selectUsersForUpdate();

            if (isNotEmpty(users))
                for (User u : users)
                    task.handleUserUpdate(u);
        } catch (Throwable t) {
            LOG.error("Job ended with error.", t);
        } finally {
            LOG.info("finished!");
        }
    }

    public List<User> selectUsersForUpdate() {
        List<User> users = userRepository.findUsersForUpdate(new DateTime().minusDays(1).getMillis(),  new PageRequest(0, 1000));
        LOG.info("Will try to update {} users.", users.size());
        return users;
    }

}
