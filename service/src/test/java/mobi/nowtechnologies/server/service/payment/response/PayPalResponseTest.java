package mobi.nowtechnologies.server.service.payment.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.junit.Test;

public class PayPalResponseTest {
	
	@Test
	public void createExpiredResponse_Successful() {
		PayPalResponse response = new PayPalResponse(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			@Override
			public String getMessage() {
				return "Expired";
			}
		});
		
		assertNotNull(response);
		assertEquals(false, response.isSuccessful());
		assertEquals("Expired", response.getDescriptionError());
	}
	
}