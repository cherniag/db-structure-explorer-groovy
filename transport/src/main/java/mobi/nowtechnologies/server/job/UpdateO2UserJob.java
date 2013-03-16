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
    private transient UserLogRepository userLogRepository;
    private transient SubscriberService subscriberService;
    private transient SubscriberPortDecorator port;
    private String username;
    private String password;
    private String endpoint;

    public UpdateO2UserJob() {
        userRepository = (UserRepository) SpringContext.getBean("userRepository");
        userLogRepository = (UserLogRepository) SpringContext.getBean("userLogRepository");
        subscriberService = (SubscriberService) SpringContext.getBean("saop.subscriberService");
        port = subscriberService.getSubscriberPortDecorator();
        port.setHandler(new SOAPLoggingHandler());
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("starting ...");
        try {
            port.setEndpoint(endpoint);
            port.setHandler(new SecurityHandler(username, password));

            List<User> users = selectUsersForUpdate();

            if (isNotEmpty(users))
                for (User u : users)
                    handleUserUpdate(u);
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

    @Transactional
    public void handleUserUpdate(User u) {
        try {
            updateUser(u);
        } catch (GetSubscriberProfileFault e) {
            makeUserLog(u, UserLogStatus.O2_FAIL, e.getFaultInfo().toString());
        } catch (Throwable t) {
            makeUserLog(u, UserLogStatus.FAIL, getStackTrace(t));
        } finally {
            LOG.info("Finished update user[{}]", u.getMobile());
        }
    }

    private void updateUser(User u) throws GetSubscriberProfileFault {
        SubscriberProfileType profile = port.getSubscriberProfile(u.getMobile());
        u.setSegment(profile.getSegmentType());
        u.setProvider(profile.getOperator());
        userRepository.save(u);
        makeUserLog(u, UserLogStatus.SUCCESS, null);
    }

    private void makeUserLog(User u, UserLogStatus status, String description) {
        UserLog oldLog = userLogRepository.findByUser(u.getId());
        UserLog userLog = new UserLog(oldLog, u.getId(), status, description);

        userLogRepository.save(userLog);
        if (UserLogStatus.SUCCESS == status)
            LOG.info("User[{}] segment[{}] updated.", u.getMobile(), u.getSegment());
        else
            LOG.error("Error on update user[{}]. [{}]. {}", u.getMobile(), userLog, description);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
