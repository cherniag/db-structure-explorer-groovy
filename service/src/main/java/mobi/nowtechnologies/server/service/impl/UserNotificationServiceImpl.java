package mobi.nowtechnologies.server.service.impl;

import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserNotificationServiceImpl implements UserNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationServiceImpl.class);

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Async
	@Override
	public Future<Boolean> notifyUserAboutSuccesfullPayment(User user) {
		try {
			LOGGER.debug("input parameters user: [{}]", user);
			if (user == null)
				throw new NullPointerException("The parameter user is null");

			LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity()
					.getName()), UserNotificationService.class);

			Future<Boolean> result;
			try {

				result = userService.makeSuccesfullPaymentFreeSMSRequest(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				result = new AsyncResult<Boolean>(Boolean.FALSE);
			}
			LOGGER.info("Output parameter result=[{}]", result);
			return result;
		} finally {
			LogUtils.removePaymentMDC();
		}
	}

}
