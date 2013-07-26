package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import com.google.common.collect.Lists;

/**
 * updates user with O2 subscriber information
 * (segment/contract/provider/4G/directChannel)
 */
public class O2UserDetailsUpdater {

	/** Updates given user */
	public void setUserFieldsFromSubscriberData(User user, O2SubscriberData data) {
		user.setProvider((data.isProviderO2() ? ProviderType.O2 : ProviderType.NON_O2).toString());
		user.setSegment(data.isBusinessOrConsumerSegment() ? SegmentType.BUSINESS : SegmentType.CONSUMER);
		user.setContract(data.isContractPostPay() ? Contract.PAYM : Contract.PAYG);
		user.setTariff(data.isTariff4G() ? Tariff._4G : Tariff._3G);
		user.setContractChannel(data.isDirect4GChannel() ? ContractChannel.DIRECT : ContractChannel.INDIRECT);
	}

	/** @return list of fields that differ */
	public List<String> getDifferences(O2SubscriberData data, User user) {
		O2SubscriberData userData = read(user);

		List<String> differences = Lists.newArrayList();

		if (userData.isBusinessOrConsumerSegment() != data.isBusinessOrConsumerSegment()) {
			differences.add("segment");
		}

		if (userData.isContractPostPayOrPrePay() != data.isContractPostPayOrPrePay()) {
			differences.add("contract");
		}

		if (userData.isProviderO2() != data.isProviderO2()) {
			differences.add("provider");
		}

		if (userData.isTariff4G() != data.isTariff4G()) {
			differences.add("tariff4G");
		}

		if (userData.isDirectOrIndirect4GChannel() != data.isDirectOrIndirect4GChannel()) {
			differences.add("direct4GChannel");
		}
		return differences;
	}

	/** creates instance of O2Subscriber data based on given user */
	public O2SubscriberData read(User user) {
		O2SubscriberData data = new O2SubscriberData();
		data.setBusinessOrConsumerSegment(user.getSegment() == SegmentType.BUSINESS);
		data.setContractPostPayOrPrePay(user.getContract() == Contract.PAYM);
		data.setProviderO2(ProviderType.O2.toString().equals(user.getProvider()));
		data.setTariff4G(user.getTariff() == Tariff._4G);
		data.setDirectOrIndirect4GChannel(user.getContractChannel() == ContractChannel.DIRECT);
		return data;
	}

}
