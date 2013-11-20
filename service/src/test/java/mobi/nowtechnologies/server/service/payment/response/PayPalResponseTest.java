package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PayPalResponseTest {
	
	@Test
	public void createExpiredResponse_Successful() {
		PayPalResponse response = new PayPalResponse(
                PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,"Expired")
        );
		assertNotNull(response);
		assertEquals(false, response.isSuccessful());
		assertEquals("Expired", response.getDescriptionError());
	}
	
}