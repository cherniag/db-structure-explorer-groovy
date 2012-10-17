package mobi.nowtechnologies.server.service.payment.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.junit.Test;

public class MigResponseTest {
	
	@Test
	public void createExpiredResponse_Successful() {
		
		MigResponse response = new MigResponse(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			
			@Override public String getMessage() {
				return "Expired";
			}
		});
		
		assertNotNull(response);
		assertEquals(false, response.isSuccessful());
		assertEquals("Expired", response.getDescriptionError());
	}
	
	@Test
	public void getExternalTxId() {
		final String txId = "2e396380-852b-4180-aec3-78b8ab2041ca";
		
		MigResponse response = new MigResponse(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			
			@Override public String getMessage() {
				return "000=[GEN] OK {Q=1 M=1 B=002 I="+txId+"}";
			}
		});
		
		assertEquals(txId, response.getExternalTxId());
	}
	
	@Test
	public void getExternalTxId_WithNoJsonFormat() {
		final String txId = "2e396380-852b-4180-aec3-78b8ab2041ca";
		
		MigResponse response = new MigResponse(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			
			@Override public String getMessage() {
				return "000=[GEN] OK {Q=1_M=1_B=002 I="+txId+"}";
			}
		});
		
		assertEquals(null, response.getExternalTxId());
	}
}