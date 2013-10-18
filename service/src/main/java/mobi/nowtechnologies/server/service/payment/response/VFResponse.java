package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.o2.soa.chargecustomerdata.BillSubscriberResponse;
import uk.co.o2.soa.chargecustomerdata.ServiceResult;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class VFResponse extends PaymentSystemResponse implements Parser<DeliverSm, VFResponse> {

	private static final BillSubscriberResponse BILL_SUBSCRIBER_RESPONSE;

	private static final Logger LOGGER = LoggerFactory.getLogger(VFResponse.class);

	static{
		BILL_SUBSCRIBER_RESPONSE = new BillSubscriberResponse();

		BILL_SUBSCRIBER_RESPONSE.setResult(new ServiceResult());
	}

	private String externalTxId;

	public static VFResponse successfulResponse() {
		return new VFResponse(BILL_SUBSCRIBER_RESPONSE, new BasicResponse() {
			@Override
			public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}

			@Override
			public String getMessage() {
				return "";
			}
		});
	}

	public static VFResponse failResponse(final String message) {
		final VFResponse failO2Response = new VFResponse(null, new BasicResponse() {
			@Override
			public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}

			@Override
			public String getMessage() {
				return message;
			}
		});
		return failO2Response;
	}

	public static VFResponse valueOf(Object objectResponse) {
		final BasicResponse response = new BasicResponse();
		final int statusCode = HttpServletResponse.SC_OK;
		final String message;

		if (objectResponse == null) {
			message = null;
		} else {
			message = objectResponse.toString();
		}

		response.setStatusCode(statusCode);
		response.setMessage(message);

		VFResponse o2Response = new VFResponse(objectResponse, response);
		return o2Response;
	}

	public VFResponse(Object objectResponse, BasicResponse response) {
		super(response, false);

	}

    public VFResponse() {
        super(new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return "";
            }
        }, true);

    }
	
	public void setExternalTxId(String externalTxId) {
		this.externalTxId = externalTxId;
	}

	public String getExternalTxId() {
		return externalTxId;
	}

	@Override
	public String toString() {
		return "O2Response [externalTxId=" + externalTxId + ", " + super.toString() + "]";
	}

    @Override
    public VFResponse parse(DeliverSm receipt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
