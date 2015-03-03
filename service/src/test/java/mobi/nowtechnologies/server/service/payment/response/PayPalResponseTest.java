package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;

import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import static org.junit.Assert.*;

public class PayPalResponseTest {

    @Test
    public void createExpiredResponse_Successful() {
        PayPalResponse response = new PayPalResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "Expired"));
        assertNotNull(response);
        assertEquals(false, response.isSuccessful());
        assertEquals("Expired", response.getDescriptionError());
    }

}