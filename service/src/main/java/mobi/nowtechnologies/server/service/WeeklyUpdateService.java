package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WeeklyUpdateService
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class WeeklyUpdateService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyUpdateService.class);

	private UserDao userDao;
	private UserService userService;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void updateWeekly() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("Job start");
			userService.updateWeekly();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LOGGER.info("Job finish");
			LogUtils.removeClassNameMDC();
		}
	}

}