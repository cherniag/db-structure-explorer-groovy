package mobi.nowtechnologies.server.service.impl;


import mobi.nowtechnologies.server.service.o2.impl.O2TariffServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway;
import static mobi.nowtechnologies.server.service.impl.o2.PhoneNumbers.O2_3G_CONTRACT;
import static mobi.nowtechnologies.server.service.impl.o2.PhoneNumbers.O2_4G_BOLTON;
import static mobi.nowtechnologies.server.service.impl.o2.PhoneNumbers.O2_4G_CONTRACT;
import static mobi.nowtechnologies.server.service.impl.o2.PhoneNumbers.O2_4G_CONTRACT2;

import javax.xml.bind.JAXBElement;

import java.util.List;

import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltons;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpayboltonsdata_2.MyCurrentBoltonsType;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContract;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * lach : 17/07/2013 : 11:41
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class O2TariffServiceIT {

    @Mock
    private WebServiceGateway webServiceGateway;

    private O2TariffServiceImpl o2TariffService;

    @Test
    public void testGetManagePostpayContractDirectToO2TestServer() throws Exception {

        mockGetContract("4G_VOICE", O2_4G_CONTRACT);

        GetContractResponse contractResponse = o2TariffService.getManagePostpayContract(O2_4G_CONTRACT);

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        mockGetContract("4G_VOICE", O2_4G_CONTRACT2);

        contractResponse = o2TariffService.getManagePostpayContract(O2_4G_CONTRACT2);

        assertEquals("4G_VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());

        mockGetContract("VOICE", O2_3G_CONTRACT);

        contractResponse = o2TariffService.getManagePostpayContract(O2_3G_CONTRACT);

        assertEquals("VOICE", contractResponse.getCurrentContract().getTariff().getProductClassification());
    }

    @Test
    public void testGetManagePostpayCurrentBoltonsDirectToO2TestServer() throws Exception {

        mockGetCurrentBoltons("Data Option", O2_4G_CONTRACT);

        GetCurrentBoltonsResponse getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons(O2_4G_CONTRACT);

        assertEquals("Data Option", getProductClassification(getCurrentBoltonsResponse));

        mockGetCurrentBoltons("4G Bolt On", O2_4G_BOLTON);

        getCurrentBoltonsResponse = o2TariffService.getManagePostpayCurrentBoltons(O2_4G_BOLTON);

        assertEquals("4G Bolt On", getProductClassification(getCurrentBoltonsResponse));

        mockGetCurrentBoltons("Data Option", O2_3G_CONTRACT);

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

    private void mockGetContract(final String productClassification, final String phone) {
        GetContractResponse response = new GetContractResponse();
        ServiceContractType contractType = new ServiceContractType();
        ProductType productType = new ProductType();
        productType.setProductClassification(productClassification);
        contractType.setTariff(productType);
        response.setCurrentContract(contractType);

        when(webServiceGateway.sendAndReceive(anyString(), argThat(new ArgumentMatcher<JAXBElement<GetContract>>() {
            public boolean matches(Object argument) {
                return argument != null && ((JAXBElement<GetContract>) argument).getValue().getCustomerId().getMsisdn().equals(phone);
            }
        }))).thenReturn(response);
    }

    private void mockGetCurrentBoltons(final String productClassification, final String phone) {
        GetCurrentBoltonsResponse response = new GetCurrentBoltonsResponse();
        MyCurrentBoltonsType currentBoltons = new MyCurrentBoltonsType();
        ProductType productType = new ProductType();
        currentBoltons.getBolton().add(productType);
        productType.setProductClassification(productClassification);
        response.setMyCurrentBoltons(currentBoltons);

        when(webServiceGateway.sendAndReceive(anyString(), argThat(new ArgumentMatcher<Object>() {
            public boolean matches(Object argument) {
                return argument != null && ((JAXBElement<GetCurrentBoltons>) argument).getValue().getCustomerId().getMsisdn().equals(phone);
            }
        }))).thenReturn(response);
    }

    @Before
    public void setUp() {
        o2TariffService = new O2TariffServiceImpl();
        o2TariffService.setWebServiceGateway(webServiceGateway);
    }
}
