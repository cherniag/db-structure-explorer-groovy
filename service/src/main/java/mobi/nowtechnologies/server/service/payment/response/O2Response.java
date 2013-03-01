package mobi.nowtechnologies.server.service.payment.response;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class O2Response extends PaymentSystemResponse {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(O2Response.class);

	public O2Response(Response response) {
		super(response);
	}
	
	public static O2Response successfulO2Response() {
		return new O2Response(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			@Override public String getMessage() {
				return "";
			}
		});
	}
	
	public static O2Response failO2Response(final String message) {
		final O2Response failO2Response = new O2Response(new Response() {
			@Override public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}
			@Override public String getMessage() {
				return message;
			}
		});
		return failO2Response;
	}

}
