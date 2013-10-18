package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Processor;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserDetailsUpdater<T extends SubscriberData> extends Processor<T>{
    User setUserFieldsFromSubscriberData(User user, T subsriberData);
}
