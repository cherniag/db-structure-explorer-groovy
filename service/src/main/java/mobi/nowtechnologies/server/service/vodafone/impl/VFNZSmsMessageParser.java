package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.shared.Parser;

public class VFNZSmsMessageParser implements Parser<String, VFNZSubscriberData> {

    @Override
    public VFNZSubscriberData parse(String data) {
        ProviderType providerType = "offnet".equalsIgnoreCase(data) ? ProviderType.NON_VF : ProviderType.VF;

        return new VFNZSubscriberData().withProvider(providerType);
    }
}