package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Processor;

/**
 * User: Alexsandr_Kolpakov Date: 10/2/13 Time: 12:32 PM
 */
public interface UserDetailsUpdater<T extends SubscriberData> extends Processor<T> {

    User setUserFieldsFromSubscriberData(User user, T subsriberData);
}
