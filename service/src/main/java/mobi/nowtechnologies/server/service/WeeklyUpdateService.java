package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * WeeklyUpdateService
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class WeeklyUpdateService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyUpdateService.class);

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void updateWeekly() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("Job start");
			process();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LOGGER.info("Job finish");
			LogUtils.removeClassNameMDC();
		}
	}
	
	public void process() {
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		LOGGER.info("weekly update job found [{}] users for update", users.size());
		for (User user : users) {
			try {
				MDC.put(LogUtils.LOG_USER_NAME, user.getUserName());
				MDC.put(LogUtils.LOG_USER_ID, user.getId());
				
				userService.saveWeeklyPayment(user);
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				MDC.remove(LogUtils.LOG_USER_NAME);
				MDC.remove(LogUtils.LOG_USER_ID);
			}
		}
	}

}