package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.data.BasicUserDetailsUpdater;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mobi.nowtechnologies.server.service.sms.BasicSMSMessageProcessor;
import org.jsmpp.bean.DeliverSm;

public class VFNZUserDetailsUpdater extends BasicSMSMessageProcessor<VFNZSubscriberData> implements UserDetailsUpdater<VFNZSubscriberData>{
    Logger LOGGER = LoggerFactory.getLogger(VFNZUserDetailsUpdater.class);
    
    private String providerNumber;
    
    private BasicUserDetailsUpdater<VFNZSubscriberData> userDetailsUpdater = new BasicUserDetailsUpdater<VFNZSubscriberData>() {
        @Override
        public User setUserFieldsFromSubscriberData(User user, VFNZSubscriberData subsriberData) {
            return VFNZUserDetailsUpdater.this.setUserFieldsFromSubscriberData(user, subsriberData);
        }
    };

	public User setUserFieldsFromSubscriberData(User user, SubscriberData subscriberData) {
        LOGGER.info("Attempt to set user fields from subscriber data [{}], [{}]", user, subscriberData);

    public void setUserService(UserService userService){
        userDetailsUpdater.setUserService(userService);
    }

    public void setProviderNumber(String providerNumber) {
        this.providerNumber = providerNumber;
    }

    public User setUserFieldsFromSubscriberData(User user, VFNZSubscriberData subsriberData) {
        VFNZSubscriberData data = (VFNZSubscriberData)subsriberData;

        if(isNull(data)){
            user.setProvider(ProviderType.VF);
        } else {
            user.setProvider(data.getProvider());
        }

        return user;
    }

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return !deliverSm.isSmscDeliveryReceipt() && providerNumber.contains(deliverSm.getSourceAddr());
    }

    @Override
    public void process(VFNZSubscriberData data) {
        userDetailsUpdater.process(data);
    }
}
