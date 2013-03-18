package mobi.nowtechnologies.server.job;

import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.apache.log4j.MDC;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class Before48hPSMSPaymentJob extends QuartzJobBean implements StatefulJob{
	private static final Logger LOGGER = LoggerFactory.getLogger(Before48hPSMSPaymentJob.class);
	
	private static final Pageable PAGEABLE_FOR_BEFORE_48H_PAYMENT_JOB = new PageRequest(0, 1000, new Sort(Direction.ASC, "nextSubPayment"));

	private UserService userService;

	@Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Before 48h Expire PSMS Payment job...");
			
			List<User> users = userService.findBefore48hExpireUsers(getEpochSeconds(), PAGEABLE_FOR_BEFORE_48H_PAYMENT_JOB);
			LOGGER.info("Before 48h Expire PSMS Payment [{}] users for handling", users.size());
			for (User user : users) {
				try {
					MDC.put(LogUtils.LOG_USER_NAME, user.getUserName());
					MDC.put(LogUtils.LOG_USER_ID, user.getId());
					
					user.setLastBefore48SmsMillis(Utils.getEpochMillis());
					userService.updateLastBefore48SmsMillis(user.getLastBefore48SmsMillis(), user.getId());
					
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					MDC.remove(LogUtils.LOG_USER_NAME);
					MDC.remove(LogUtils.LOG_USER_ID);
				}
			}
			
			LOGGER.info("[DONE] Before 48h Expire PSMS Payment job");
		} catch (Exception e) {
			LOGGER.error("Error while Before 48h Expire PSMS Payment job. {}", e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}