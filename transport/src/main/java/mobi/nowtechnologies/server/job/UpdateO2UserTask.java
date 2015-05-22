package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import static mobi.nowtechnologies.server.shared.log.LogUtils.putGlobalMDC;
import static mobi.nowtechnologies.server.shared.log.LogUtils.removeGlobalMDC;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

import org.springframework.transaction.annotation.Transactional;

public class UpdateO2UserTask {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateO2UserTask.class);
    private transient UserLogRepository userLogRepository;
    private transient O2Service o2Service;
    private transient UserService userService;
    private transient DevicePromotionsService deviceService;
    private transient O2UserDetailsUpdater o2UserDetailsUpdater = new O2UserDetailsUpdater();

    @Transactional
    public void handleUserUpdate(User u) {
        long beforeExecutionTimeNano = System.nanoTime();
        Throwable error = null;
        try {
            putGlobalMDC(u.getId(), u.getMobile(), u.getUserName(), u.getUserGroup().getCommunity().getRewriteUrlParameter(), "", UpdateO2UserTask.class, "");
            updateUser(u);
        } catch (Throwable t) {
            error = t;
            LOG.error("Can't update user id=[{}] phone=[{}] error=[{}]", u.getId(), u.getMobile(), t, t);
            makeUserLog(u, UserLogStatus.FAIL, getStackTrace(t));
        } finally {
            long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);
            String result = "OK";
            if (error != null) {
                result = "Error:" + error;
            }
            LOG.info("updateUser completed in [{}]ms user id=[{}] phone [{}] result: [{}]", executionDurationMillis, u.getId(), u.getMobile(), result);
            removeGlobalMDC();
        }
    }

    private void updateUser(User u) {
        LOG.info("getting subscriber data for phone [{}], id=[{}]", u.getMobile(), u.getId());
        O2SubscriberData o2SubscriberData;
        if (deviceService.isPromotedDevicePhone(u.getUserGroup().getCommunity(), u.getMobile(), null)) {
            o2SubscriberData = o2UserDetailsUpdater.getDefaultSubscriberData();
            LOG.info("[promoted device] default subscriber data for [{}]", u.getMobile());
        } else {
            o2SubscriberData = o2Service.getSubscriberData(u.getMobile());
        }
        LOG.debug("subscriber data: [{}] ", o2SubscriberData);

        makeUserLog(u, UserLogStatus.SUCCESS, null);

        Collection<String> changes = o2UserDetailsUpdater.getDifferences(o2SubscriberData, u);
        if (changes.isEmpty()) {
            LOG.debug("no changes for user:[{}] mobile:[{}]", u.getId(), u.getMobile());
        } else {
            LOG.info("updating user:[{}] mobile:[{}] changes:[{}]", u.getId(), u.getMobile(), changes);
            userService.o2SubscriberDataChanged(u, o2SubscriberData);
            LOG.info("user o2 details updated:[{}] mobile:[{}] changes:[{}]", u.getId(), u.getMobile(), changes);
        }
    }

    private void makeUserLog(User u, UserLogStatus status, String description) {
        long beforeExecutionTimeNano = System.nanoTime();
        UserLog oldLog = userLogRepository.findByUser(u.getId(), UserLogType.UPDATE_O2_USER);
        UserLog userLog = new UserLog(oldLog, u, status, UserLogType.UPDATE_O2_USER, description);

        userLogRepository.save(userLog);
        long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);

        if (UserLogStatus.SUCCESS == status) {
            LOG.info("User[{}] segment[{}] updated in [{}]ms.", u.getMobile(), u.getSegment(), executionDurationMillis);
        } else {
            LOG.error("Error on update user[{}]. [{}]. {} in [{}]ms", u.getMobile(), userLog, description, executionDurationMillis);
        }
    }

    public void setUserLogRepository(UserLogRepository userLogRepository) {
        this.userLogRepository = userLogRepository;
    }

    public void setO2Service(O2Service o2Service) {
        this.o2Service = o2Service;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDeviceService(DevicePromotionsService deviceService) {
        this.deviceService = deviceService;
    }
}
