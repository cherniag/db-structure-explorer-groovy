package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.SubscriberData;

/** Represents O2 mobile network subscriber information */
public class VFNZSubscriberData extends SubscriberData {

    public VFNZSubscriberData withProvider(ProviderType provider) {
        super.withProvider(provider);

        return this;
    }
}
