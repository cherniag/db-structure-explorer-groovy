package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;

import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import static org.junit.Assert.*;

public class MigResponseTest {

    @Test
    public void createExpiredResponse_Successful() {
        MigResponse response = new MigResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "Expired"));
        assertNotNull(response);
        assertEquals(false, response.isSuccessful());
        assertEquals("Expired", response.getDescriptionError());
    }

    @Test
    public void getExternalTxId() {
        final String txId = "2e396380-852b-4180-aec3-78b8ab2041ca";
        MigResponse response = new MigResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "000=[GEN] OK {Q=1 M=1 B=002 I=" + txId + "}"));
        assertEquals(txId, response.getExternalTxId());
    }

    @Test
    public void getExternalTxId_WithNoJsonFormat() {
        final String txId = "2e396380-852b-4180-aec3-78b8ab2041ca";
        MigResponse response = new MigResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "000=[GEN] OK {Q=1_M=1_B=002 I=" + txId + "}"));
        assertEquals(null, response.getExternalTxId());
    }
}