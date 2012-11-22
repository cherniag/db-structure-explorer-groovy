package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.concurrent.Future;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserNotificationService {

	Future<Boolean> notifyUserAboutSuccesfullPayment(User user);
}
