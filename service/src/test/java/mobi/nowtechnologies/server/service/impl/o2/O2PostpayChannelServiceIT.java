package mobi.nowtechnologies.server.service.impl.o2;

import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@Ignore
public class O2PostpayChannelServiceIT {

    protected final Logger LOGGER = LoggerFactory.getLogger(O2PostpayChannelServiceIT.class);

    @Autowired
    private O2Service o2service;

    @Test
    public void testService() throws Exception {
        LOGGER.info("start");

        KeystoreUtils.initKeystore();

        O2SubscriberData res = o2service.getSubscriberData(PhoneNumbers._4G_DIRECT_CHANNEL);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());


        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_INDIRECT_P4U);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertFalse(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_INDIRECT_TESCO);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertFalse(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_INDIRECT_MASS_DISTRIBUTION);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertFalse(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_INDIRECT_ESME);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertFalse(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_INDIRECT_CPW);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertFalse(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_UPGRADE_CONSUMER);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_UPGRADE_CSA);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_RETAIL_SHOP);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_LBM2_IN);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_CONSUMER);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_LBM2_OUT);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        res = o2service.getSubscriberData(PhoneNumbers.POSTPAY_DIRECT_AGENT);
        assertTrue(res.isProviderO2());
        assertFalse(res.isBusinessOrConsumerSegment());
        assertTrue(res.isContractPostPayOrPrePay());
        assertTrue(res.isDirectOrIndirect4GChannel());
        assertTrue(res.isTariff4G());

        LOGGER.info("competed");
    }

}
