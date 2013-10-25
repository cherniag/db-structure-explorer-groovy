package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 12:32 PM
 */
public interface UserDetailsUpdater {
    User setUserFieldsFromSubscriberData(User user, SubscriberData subsriberData);
}
