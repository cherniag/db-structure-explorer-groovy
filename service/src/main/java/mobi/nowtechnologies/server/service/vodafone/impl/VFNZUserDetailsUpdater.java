package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

public class VFNZUserDetailsUpdater implements UserDetailsUpdater{

    Logger LOGGER = LoggerFactory.getLogger(VFNZUserDetailsUpdater.class);

	public User setUserFieldsFromSubscriberData(User user, SubscriberData subscriberData) {
        LOGGER.info("Attempt to set user fields from subscriber data [{}], [{}]", user, subscriberData);

        VFNZSubscriberData data = (VFNZSubscriberData) subscriberData;

        if(isNull(data)){
            user.setProvider(ProviderType.VF);
        } else {
            user.setProvider(data.getProvider());
        }

        return user;
    }
}
