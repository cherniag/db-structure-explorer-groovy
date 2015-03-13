package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

import com.sentaca.spring.smpp.mo.MOMessage;

public class VFNZSubscriberDataParser implements Parser<MOMessage, VFNZSubscriberData> {

    @Override
    public VFNZSubscriberData parse(MOMessage data) {
        String text = data.getText();

        String phoneNumber = "+" + data.getDestAddress();
        ProviderType providerType = "offnet".equalsIgnoreCase(text) ?
                                    ProviderType.NON_VF :
                                    ProviderType.VF;

        return new VFNZSubscriberData().withProvider(providerType).withPhoneNumber(phoneNumber);
    }
}