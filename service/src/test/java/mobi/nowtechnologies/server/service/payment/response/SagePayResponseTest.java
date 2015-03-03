package mobi.nowtechnologies.server.service.payment.response;


import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;

import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import static org.junit.Assert.*;

public class SagePayResponseTest {

    @Test
    public void createExpiredResponse_Successful() {
        SagePayResponse response = new SagePayResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "Expired"));

        assertNotNull(response);
        assertEquals(false, response.isSagePaySuccessful());
    }
}