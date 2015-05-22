package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

// @author Titov Mykhaylo (titov) on 02.03.2015.
public interface MessageNotificationService {
    String getMessage(User user, String msgCodeBase, String[] msgArgs);
}
