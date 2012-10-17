package mobi.nowtechnologies.server.service.payment.response;


import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

import static org.junit.Assert.*;
import org.junit.Test;

public class SagePayResponseTest {
	
	@Test
	public void createExpiredResponse_Successful() {
		SagePayResponse response = new SagePayResponse(new Response(){
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			public String getMessage() {
				return "Expired";
			};
		});
		
		assertNotNull(response);
		assertEquals(false, response.isSagePaySuccessful());
	}
}