package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.SubsriberData;

/** Represents O2 mobile network subscriber information */
public class VFNZSubscriberData extends SubsriberData{

    public VFNZSubscriberData withProvider(ProviderType provider) {
        super.withProvider(provider);

        return this;
    }
}
