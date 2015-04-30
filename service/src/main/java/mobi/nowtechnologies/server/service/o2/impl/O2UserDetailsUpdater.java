package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.data.BasicUserDetailsUpdater;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * updates user with O2 subscriber information (segment/contract/provider/4G/directChannel)
 */
public class O2UserDetailsUpdater extends BasicUserDetailsUpdater<O2SubscriberData> {
    /**
     * Updates given user
     */
    @Override
    public User setUserFieldsFromSubscriberData(User user, O2SubscriberData o2SubscriberData) {
        O2SubscriberData data = o2SubscriberData;

        if (data == null) {
            data = O2SubscriberData.DEFAULT_SUBSCRIBER_DATA;
        }

        user.setProvider(data.isProviderO2() ?
                         ProviderType.O2 :
                         ProviderType.NON_O2);
        user.setSegment(data.isBusinessOrConsumerSegment() ?
                        SegmentType.BUSINESS :
                        SegmentType.CONSUMER);
        user.setContract(data.isContractPostPay() ?
                         Contract.PAYM :
                         Contract.PAYG);
        user.setTariff(data.isTariff4G() ?
                       Tariff._4G :
                       Tariff._3G);
        user.setContractChannel(data.isDirect4GChannel() ?
                                ContractChannel.DIRECT :
                                ContractChannel.INDIRECT);
        return user;
    }

    /**
     * @return list of fields that differ
     */
    public List<String> getDifferences(O2SubscriberData data, User user) {
        O2SubscriberData userData = read(user);

        List<String> differences = Lists.newArrayList();

        // if the contract/provider/segment is set to null in user object, we still set data in O2SubscriberData object...
        // a case that's failing is user.segment == null, and in the data we receive from o2 user segment is CONTRACT
        // the old logic would not see a difference
        if (user.getSegment() == null || userData.isBusinessOrConsumerSegment() != data.isBusinessOrConsumerSegment()) {
            differences.add("segment");
        }

        if (user.getContract() == null || userData.isContractPostPayOrPrePay() != data.isContractPostPayOrPrePay()) {
            differences.add("contract");
        }

        if (user.getProvider() == null || userData.isProviderO2() != data.isProviderO2()) {
            differences.add("provider");
        }

        if (user.getTariff() == null || userData.isTariff4G() != data.isTariff4G()) {
            differences.add("tariff4G");
        }

        if (user.getContractChannel() == null || userData.isDirectOrIndirect4GChannel() != data.isDirectOrIndirect4GChannel()) {
            differences.add("direct4GChannel");
        }
        return differences;
    }

    /**
     * creates instance of O2Subscriber data based on given user
     */
    public O2SubscriberData read(User user) {
        O2SubscriberData data = new O2SubscriberData();
        data.setBusinessOrConsumerSegment(user.getSegment() == SegmentType.BUSINESS);
        data.setContractPostPayOrPrePay(user.getContract() == Contract.PAYM);
        data.setProviderO2(ProviderType.O2.equals(user.getProvider()));
        data.setTariff4G(user.getTariff() == Tariff._4G);
        data.setDirectOrIndirect4GChannel(user.getContractChannel() == ContractChannel.DIRECT);
        return data;
    }
}
