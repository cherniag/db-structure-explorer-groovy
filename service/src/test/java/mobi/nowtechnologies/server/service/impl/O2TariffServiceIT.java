package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.service.O2TariffService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * lach : 17/07/2013 : 11:41
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class O2TariffServiceIT {

    @Resource(name = "service.O2TariffService")
    private O2TariffService o2TariffService;

    @Test
    public void testGetManagePostpayContractDirectToO2TestServer() throws Exception {

//        447764119970 – with 4G tariff
//        447764119969 – with 4G tariff
//        447764119984 – with 3G Tariff
//        447764119989 – with 3G Tariff

        GetContractResponse contractResponse = o2TariffService.getManagePostpayContract("447764119970");

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract("447764119969");

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract("447764119984");

        assertEquals("VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract("447764119984");

        assertEquals("VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());


    }

    @Test
    public void testGetManagePostpayCurrentBoltonsDirectToO2TestServer() throws Exception {

//        447764119970 – with 4G tariff
//        447764119980 – with 4G Bolton
//        447764119984 – with 3G Tariff

        GetCurrentBoltonsResponse getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons("447764119970");

        assertEquals("Data Option", getProductClassification(getCurrentBoltonsResponse));

        getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons("447764119980");

        assertEquals("4G Bolt On", getProductClassification(getCurrentBoltonsResponse));

        getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons("447764119984");

        assertEquals("Data Option", getProductClassification(getCurrentBoltonsResponse));


    }

    @Test
    public void testGetManagePrepayTariffDirectToO2TestServer() throws Exception {

        //TODO LH some prepay phone numbers are


    }

    private String getProductClassification(GetCurrentBoltonsResponse getCurrentBoltonsResponse) {
        List<ProductType> productTypes = getCurrentBoltonsResponse.getMyCurrentBoltons().getBolton();
        String productClassification = null;
        for (ProductType productType : productTypes) {
            productClassification = productType.getProductClassification();
        }
        return productClassification;
    }
}
