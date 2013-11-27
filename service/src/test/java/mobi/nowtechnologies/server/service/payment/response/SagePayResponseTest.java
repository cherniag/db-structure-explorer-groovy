package mobi.nowtechnologies.server.service.payment.response;


import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SagePayResponseTest {
	
	@Test
	public void createExpiredResponse_Successful() {
		SagePayResponse response = new SagePayResponse(new BasicResponse(){
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