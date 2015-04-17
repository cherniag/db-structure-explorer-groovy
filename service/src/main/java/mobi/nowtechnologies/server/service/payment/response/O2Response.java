package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.o2.soa.chargecustomerdata.BillSubscriberResponse;
import uk.co.o2.soa.chargecustomerdata.ServiceResult;
import uk.co.o2.soa.chargecustomerservice.BillSubscriberFault;
import uk.co.o2.soa.coredata.SOAFaultType;

/**
 * @author Titov Mykhaylo (titov)
 */
public class O2Response extends PaymentSystemResponse {

    private static final BillSubscriberResponse BILL_SUBSCRIBER_RESPONSE;

    private static final Logger LOGGER = LoggerFactory.getLogger(O2Response.class);

    static {
        BILL_SUBSCRIBER_RESPONSE = new BillSubscriberResponse();

        BILL_SUBSCRIBER_RESPONSE.setResult(new ServiceResult());
    }

    private String externalTxId;

    public O2Response(Object objectResponse, BasicResponse response) {
        super(response, false);

        if (objectResponse == null) {
            isSuccessful = false;
            descriptionError = "No response";
        } else if (objectResponse instanceof BillSubscriberResponse) {
            BillSubscriberResponse billSubscriberResponse = (BillSubscriberResponse) objectResponse;

            isSuccessful = true;
            final ServiceResult serviceResult = billSubscriberResponse.getResult();
            externalTxId = serviceResult.getSagTransactionId();

        } else if (objectResponse instanceof BillSubscriberFault) {
            BillSubscriberFault billSubscriberFault = (BillSubscriberFault) objectResponse;

            isSuccessful = false;
            final SOAFaultType soaFaultType = billSubscriberFault.getFaultInfo();
            descriptionError = soaFaultType.getFaultDescription();
            errorCode = soaFaultType.getSOAFaultCode();
        } else {
            throw new ServiceException("Unknown response object [" + objectResponse + "]");
        }
    }

    public static O2Response successfulO2Response() {
        return new O2Response(BILL_SUBSCRIBER_RESPONSE, new BasicResponse() {
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

    public static O2Response failO2Response(final String message) {
        final O2Response failO2Response = new O2Response(null, new BasicResponse() {
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

    public static O2Response valueOf(Object objectResponse) {
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

        O2Response o2Response = new O2Response(objectResponse, response);
        return o2Response;
    }

    public String getExternalTxId() {
        return externalTxId;
    }

    public void setExternalTxId(String externalTxId) {
        this.externalTxId = externalTxId;
    }

    @Override
    public String toString() {
        return "O2Response [externalTxId=" + externalTxId + ", " + super.toString() + "]";
    }
}
