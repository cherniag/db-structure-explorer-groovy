package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

/** Represents O2 mobile network subscriber information */
public class VFNZSubscriberData extends SubscriberData {

    public VFNZSubscriberData withProvider(ProviderType provider) {
        super.withProvider(provider);

        return this;
    }
}
