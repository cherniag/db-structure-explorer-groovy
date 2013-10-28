package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

public class VFNZSubscriberDataParser implements Parser<String, VFNZSubscriberData> {

    @Override
    public VFNZSubscriberData parse(String data) {
        ProviderType providerType = "offnet".equalsIgnoreCase(data) ? ProviderType.NON_VF : ProviderType.VF;

        return new VFNZSubscriberData().withProvider(providerType);
    }
}