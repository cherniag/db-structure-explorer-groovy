package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway;
import mobi.nowtechnologies.server.service.payment.response.O2Response;

import java.math.BigDecimal;

import uk.co.o2.soa.chargecustomerdata.BillSubscriberResponse;
import uk.co.o2.soa.chargecustomerdata.ServiceResult;
import uk.co.o2.soa.chargecustomerservice.BillSubscriberFault;
import uk.co.o2.soa.coredata.SOAFaultType;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class O2ClientServiceIT {

    private O2ProviderServiceImpl o2ClientService;

    @Mock
    private WebServiceGateway webServiceGateway;

    @Test
    public void testMakePremiumSMSRequest_Failure() throws Exception {
        // Preparations for test
        String subMerchantId = "O2 Tracks";
        String contentDescription = "Description of content";
        String contentType = "mqbed_tracks_3107054";
        String contentCategory = "other";
        String message = "";
        String o2PhoneNumber = "447511182660";
        BigDecimal subCost = new BigDecimal(1);
        String internalTxId = "";
        int userId = 1;
        boolean smsNotify = false;

        BillSubscriberFault response = new BillSubscriberFault("error", new SOAFaultType());
        when(webServiceGateway.sendAndReceive(anyString(), any())).thenReturn(response);

        // Invocation of test method
        O2Response result = o2ClientService.makePremiumSMSRequest(userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription, subMerchantId, smsNotify);

        // Asserts
        assertNotNull(result);
        assertEquals(false, result.isSuccessful());
    }

    @Test
    public void testMakePremiumSMSRequest_Successful() throws Exception {
        // Preparations for test
        String subMerchantId = "O2 Tracks";
        String contentDescription = "Description of content";
        String contentType = "mqbed_tracks_3107054";
        String contentCategory = "other";
        String message = "gfgfg";
        String o2PhoneNumber = "447511182664";
        BigDecimal subCost = new BigDecimal(1);
        String internalTxId = "";
        int userId = 1;
        boolean smsNotify = false;

        BillSubscriberResponse response = new BillSubscriberResponse();
        response.setResult(new ServiceResult());
        when(webServiceGateway.sendAndReceive(anyString(), any())).thenReturn(response);

        // Invocation of test method
        O2Response result = o2ClientService.makePremiumSMSRequest(userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription, subMerchantId, smsNotify);

        // Asserts
        assertNotNull(result);
        assertEquals(true, result.isSuccessful());
    }

    @Before
    public void setUp() {
        o2ClientService = new O2ProviderServiceImpl();
        o2ClientService.setWebServiceGateway(webServiceGateway);
    }
}