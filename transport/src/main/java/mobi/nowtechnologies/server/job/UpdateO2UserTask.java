package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.co.o2.soa.subscriberdata.SubscriberProfileType;
import uk.co.o2.soa.subscriberservice.GetSubscriberProfileFault;
import uk.co.o2.soa.utils.SubscriberPortDecorator;

import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public class UpdateO2UserTask {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserTask.class);
    private transient SubscriberPortDecorator port;
    private transient UserRepository userRepository;
    private transient UserLogRepository userLogRepository;

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
        u.setContract(profile.getCotract());
        makeUserLog(u, UserLogStatus.SUCCESS, null);
        userRepository.save(u);
    }

    private void makeUserLog(User u, UserLogStatus status, String description) {
        UserLog oldLog = userLogRepository.findByUser(u.getId());
        UserLog userLog = new UserLog(oldLog, u, status, UserLogType.UPDATE_O2_USER, description);

        userLogRepository.save(userLog);
        if (UserLogStatus.SUCCESS == status)
            LOG.info("User[{}] segment[{}] updated.", u.getMobile(), u.getSegment());
        else
            LOG.error("Error on update user[{}]. [{}]. {}", u.getMobile(), userLog, description);
    }

    public void setPort(SubscriberPortDecorator port) {
        this.port = port;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserLogRepository(UserLogRepository userLogRepository) {
        this.userLogRepository = userLogRepository;
    }
}
