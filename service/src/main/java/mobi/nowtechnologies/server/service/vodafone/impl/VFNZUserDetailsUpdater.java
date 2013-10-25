package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VFNZUserDetailsUpdater implements UserDetailsUpdater{

    Logger LOGGER = LoggerFactory.getLogger(VFNZUserDetailsUpdater.class);

	public User setUserFieldsFromSubscriberData(User user, SubscriberData subsriberData) {
        LOGGER.info("Attempt to set user fields from subscriber data [{}], [{}]", user, subsriberData);

        VFNZSubscriberData data = (VFNZSubscriberData)subsriberData;

        if(data == null){
            user.setProvider(ProviderType.VF.toString());
        } else {
            user.setProvider(data.getProvider() != null ? data.getProvider().toString() : null);
        }

        return user;
    }
}
