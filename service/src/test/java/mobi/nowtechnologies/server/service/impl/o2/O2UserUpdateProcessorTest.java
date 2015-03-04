package mobi.nowtechnologies.server.service.impl.o2;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class O2UserUpdateProcessorTest {

    private O2UserDetailsUpdater p = new O2UserDetailsUpdater();

    @Test
    public void testUpdateUser() {
        O2SubscriberData data = new O2SubscriberData();

        User user = new User();

        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), CONSUMER);
        assertEquals(user.getProvider(), NON_O2);
        assertEquals(user.getTariff(), _3G);
        assertEquals(user.getContract(), PAYG);
        assertEquals(user.getContractChannel(), DIRECT);


        data.setBusinessOrConsumerSegment(true);
        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), BUSINESS);
        assertEquals(user.getProvider(), NON_O2);
        assertEquals(user.getTariff(), _3G);
        assertEquals(user.getContract(), PAYG);
        assertEquals(user.getContractChannel(), DIRECT);

        data.setContractPostPayOrPrePay(true);
        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), BUSINESS);
        assertEquals(user.getProvider(), NON_O2);
        assertEquals(user.getTariff(), _3G);
        assertEquals(user.getContract(), PAYM);
        assertEquals(user.getContractChannel(), DIRECT);

        data.setProviderO2(true);
        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), BUSINESS);
        assertEquals(user.getProvider(), O2);
        assertEquals(user.getTariff(), _3G);
        assertEquals(user.getContract(), PAYM);
        assertEquals(user.getContractChannel(), DIRECT);

        data.setTariff4G(true);
        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), BUSINESS);
        assertEquals(user.getProvider(), O2);
        assertEquals(user.getTariff(), _4G);
        assertEquals(user.getContract(), PAYM);
        assertEquals(user.getContractChannel(), DIRECT);

        data.setDirectOrIndirect4GChannel(false);
        p.setUserFieldsFromSubscriberData(user, data);
        assertEquals(user.getSegment(), BUSINESS);
        assertEquals(user.getProvider(), O2);
        assertEquals(user.getTariff(), _4G);
        assertEquals(user.getContract(), PAYM);
        assertEquals(user.getContractChannel(), INDIRECT);

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
        data.setDirectOrIndirect4GChannel(false);
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
