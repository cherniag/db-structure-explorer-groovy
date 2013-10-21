package mobi.nowtechnologies.server.service.impl;


import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.service.o2.O2TariffService;
import static mobi.nowtechnologies.server.service.impl.o2.PhoneNumbers.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;


/**
 * lach : 17/07/2013 : 11:41
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/service-test-ws.xml" })
public class O2TariffServiceIT {

	
	@Resource(name = "service.O2TariffService")
    private O2TariffService o2TariffService;

    @Test
    @Ignore
    public void testGetManagePostpayContractDirectToO2TestServer() throws Exception {

        GetContractResponse contractResponse = o2TariffService.getManagePostpayContract(O2_4G_CONTRACT);

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract(O2_4G_CONTRACT2);

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract(O2_3G_CONTRACT);

        assertEquals("VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        contractResponse = o2TariffService.getManagePostpayContract(O2_3G_CONTRACT);

        assertEquals("VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());


    }

    @Test
    @Ignore
    public void testGetManagePostpayCurrentBoltonsDirectToO2TestServer() throws Exception {

        GetCurrentBoltonsResponse getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons(O2_4G_CONTRACT);

        assertEquals("Data Option", getProductClassification(getCurrentBoltonsResponse));

        getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons(O2_4G_BOLTON);

        assertEquals("4G Bolt On", getProductClassification(getCurrentBoltonsResponse));

        getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons(O2_3G_CONTRACT);

        assertEquals("Data Option", getProductClassification(getCurrentBoltonsResponse));

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
