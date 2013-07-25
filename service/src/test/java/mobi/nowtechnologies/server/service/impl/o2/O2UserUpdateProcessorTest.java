package mobi.nowtechnologies.server.service.impl.o2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import org.junit.Test;

public class O2UserUpdateProcessorTest {
	private O2UserDetailsUpdater p = new O2UserDetailsUpdater();

	@Test
	public void testUpdateUser() {
		O2SubscriberData data = new O2SubscriberData();

		User user = new User();

		p.setUserFieldsFromSubscriberData(user, data);
		assertEquals(user.getSegment(), SegmentType.CONSUMER);
		assertEquals(user.getProvider(), ProviderType.NON_O2.toString());
		assertEquals(user.getTariff(), Tariff._3G);
		assertEquals(user.getContract(), Contract.PAYG);

		data.setBusinessOrConsumerSegment(true);
		p.setUserFieldsFromSubscriberData(user, data);
		assertEquals(user.getSegment(), SegmentType.BUSINESS);
		assertEquals(user.getProvider(), ProviderType.NON_O2.toString());
		assertEquals(user.getTariff(), Tariff._3G);
		assertEquals(user.getContract(), Contract.PAYG);

		data.setContractPostPayOrPrePay(true);
		p.setUserFieldsFromSubscriberData(user, data);
		assertEquals(user.getSegment(), SegmentType.BUSINESS);
		assertEquals(user.getProvider(), ProviderType.NON_O2.toString());
		assertEquals(user.getTariff(), Tariff._3G);
		assertEquals(user.getContract(), Contract.PAYM);

		data.setProviderO2(true);
		p.setUserFieldsFromSubscriberData(user, data);
		assertEquals(user.getSegment(), SegmentType.BUSINESS);
		assertEquals(user.getProvider(), ProviderType.O2.toString());
		assertEquals(user.getTariff(), Tariff._3G);
		assertEquals(user.getContract(), Contract.PAYM);

		data.setTariff4G(true);
		p.setUserFieldsFromSubscriberData(user, data);
		assertEquals(user.getSegment(), SegmentType.BUSINESS);
		assertEquals(user.getProvider(), ProviderType.O2.toString());
		assertEquals(user.getTariff(), Tariff._4G);
		assertEquals(user.getContract(), Contract.PAYM);

		// TODO: test Direct/Indirect when ready
	}

	@Test
	public void testDifferences() {
		O2SubscriberData data = new O2SubscriberData();
		O2SubscriberData data2 = new O2SubscriberData();

		List<String> differences = getDifferences(data, data2);
		assertTrue(differences.isEmpty());

		data.setBusinessOrConsumerSegment(true);
		differences = getDifferences(data, data2);
		assertEquals(differences.size(), 1);
		assertEquals(differences.get(0), "segment");

		data = new O2SubscriberData();
		data.setContractPostPayOrPrePay(true);
		data.setProviderO2(true);
		differences = getDifferences(data, data2);
		assertEquals(differences.size(), 2);
		assertEquals(differences.get(0), "contract");
		assertEquals(differences.get(1), "provider");

		data = new O2SubscriberData();
		data.setContractPostPayOrPrePay(true);
		data.setProviderO2(true);
		data.setTariff4G(true);
		differences = getDifferences(data, data2);
		assertEquals(differences.size(), 3);
		assertEquals(differences.get(0), "contract");
		assertEquals(differences.get(1), "provider");
		assertEquals(differences.get(2), "tariff4G");

		data = new O2SubscriberData();
		data.setContractPostPayOrPrePay(true);
		data.setProviderO2(true);
		data.setTariff4G(true);
		data.setDirectOrIndirect4GChannel(true);
		differences = getDifferences(data, data2);
		assertEquals(differences.size(), 4);
		assertEquals(differences.get(0), "contract");
		assertEquals(differences.get(1), "provider");
		assertEquals(differences.get(2), "tariff4G");
		assertEquals(differences.get(3), "direct4GChannel");
	}

	private List<String> getDifferences(O2SubscriberData data, O2SubscriberData data2) {
		User user = new User();
		p.setUserFieldsFromSubscriberData(user, data2);
		List<String> differences = p.getDifferences(data, user);
		return differences;
	}

}
