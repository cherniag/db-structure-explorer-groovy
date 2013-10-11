package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;

public class VFNZUserDetailsUpdater implements UserDetailsUpdater{

	public User setUserFieldsFromSubscriberData(User user, SubscriberData subsriberData) {
        VFNZSubscriberData data = (VFNZSubscriberData)subsriberData;

        if(data == null){
            user.setProvider(ProviderType.VF.toString());
        } else {
            user.setProvider(data.getProvider() != null ? data.getProvider().toString() : null);
        }

        return user;
    }
}
