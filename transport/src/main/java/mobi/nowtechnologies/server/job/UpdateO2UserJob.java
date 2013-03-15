package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLosStatus;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.o2.soa.subscriberdata.SubscriberProfileType;
import uk.co.o2.soa.subscriberservice.GetSubscriberProfileFault;
import uk.co.o2.soa.subscriberservice.SubscriberService;
import uk.co.o2.soa.utils.SOAPLoggingHandler;
import uk.co.o2.soa.utils.SecurityHandler;
import uk.co.o2.soa.utils.SubscriberPortDecorator;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class UpdateO2UserJob extends QuartzJobBean {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserJob.class);

    private transient UserRepository userRepository;
    private transient UserLogRepository userLogRepository;
    private transient SubscriberService subscriberService;

    public UpdateO2UserJob() {
        userRepository = (UserRepository) SpringContext.getBean("userRepository");
        userLogRepository = (UserLogRepository) SpringContext.getBean("userLogRepository");
        subscriberService = (SubscriberService) SpringContext.getBean("saop.subscriberService");
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("starting ...");

        List<User> users = selectUsersForUpdate();

        if (isNotEmpty(users))
            for (User u : users)
                handleUserUpdate(u);

        LOG.info("finished!");
    }

    public List<User> selectUsersForUpdate() {
        List<Integer> updatedUsers = userLogRepository.findUpdatedUsers(new DateTime().minusDays(1).getMillis());
        List<User> users;
        if(isEmpty(updatedUsers))
            users = (List<User>)userRepository.findAll();
        else
            users = userRepository.findUsersForUpdate(updatedUsers);
        LOG.info("Will try to update {} users.", users.size());
        return users;
    }

    @Transactional
    public void handleUserUpdate(User u) {
        try {
            updateUser(u);
        } catch (GetSubscriberProfileFault e) {
            makeUserLog(u, UserLosStatus.O2_FAIL, e);
        } catch (Throwable t) {
            makeUserLog(u, UserLosStatus.FAIL, t);
        } finally {
            LOG.info("Finished update user[{}]", u.getMobile());
        }
    }

    private void updateUser(User u) throws GetSubscriberProfileFault {
        SubscriberPortDecorator port = subscriberService.getSubscriberPortDecorator();

        //TODO
        port.setEndpoint("https://sdpapi.ref.o2.co.uk/services/Subscriber_2_0");
        port.setHandler(new SOAPLoggingHandler());
        port.setHandler(new SecurityHandler("musicQubed_1001", "BA4sWteQ"));

        SubscriberProfileType profile = port.getSubscriberProfile(u.getMobile());
        u.setSegment(profile.getSegmentType());
        userRepository.save(u);
        makeUserLog(u, UserLosStatus.SUCCESS, null);
    }

    private void makeUserLog(User u, UserLosStatus status, Throwable t) {
        UserLog userLog = new UserLog(u.getId(), System.currentTimeMillis(), status);
        if (t == null)
            LOG.info("User[{}] segment[{}] updated.", u.getMobile(), u.getSegment());
        else
            LOG.error("Error on update user[{}]. [{}]. {}", u.getMobile(), userLog, t.getMessage());
        userLogRepository.save(userLog);
    }

}
