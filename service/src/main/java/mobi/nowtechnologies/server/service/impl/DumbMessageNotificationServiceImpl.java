package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageNotificationService;

// @author Titov Mykhaylo (titov) on 03.03.2015.
public class DumbMessageNotificationServiceImpl implements MessageNotificationService {

    @Override
    public String getMessage(User user, String msgCodeBase, String[] msgArgs) {
        return null;
    }
}
