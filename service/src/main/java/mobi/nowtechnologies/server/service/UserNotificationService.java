package mobi.nowtechnologies.server.service;

import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserNotificationService {

	Future<Boolean> notifyUserAboutSuccesfullPayment(User user);
}
