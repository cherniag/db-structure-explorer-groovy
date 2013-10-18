package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessor;
import org.jsmpp.bean.DeliverSm;

public class VFNZUserDetailsUpdater extends SMSMessageProcessor<VFNZSubscriberData> implements UserDetailsUpdater<VFNZSubscriberData>{

	public User setUserFieldsFromSubscriberData(User user, VFNZSubscriberData subsriberData) {
        VFNZSubscriberData data = (VFNZSubscriberData)subsriberData;

        if(data == null){
            user.setProvider(ProviderType.VF.toString());
        } else {
            user.setProvider(data.getProvider() != null ? data.getProvider().toString() : null);
        }

        return user;
    }

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void process(VFNZSubscriberData data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
