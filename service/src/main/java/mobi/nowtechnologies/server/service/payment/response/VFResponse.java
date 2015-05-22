package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.support.http.BasicResponse;

import javax.servlet.http.HttpServletResponse;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexander Kolpakov
 */
public class VFResponse extends PaymentSystemResponse implements Parser<DeliverSm, VFResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VFResponse.class);

    private String phoneNumber;

    protected VFResponse(DeliverSm deliverSm, BasicResponse response) {
        super(response, false);

        if (deliverSm != null) {
            try {
                phoneNumber = "+" + deliverSm.getSourceAddr();
                DeliveryReceipt deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                isSuccessful = deliveryReceipt.getFinalStatus() == DeliveryReceiptState.ACCEPTD || deliveryReceipt.getFinalStatus() == DeliveryReceiptState.DELIVRD;

                if (!isSuccessful) {
                    descriptionError = deliveryReceipt.getFinalStatus().toString();
                    errorCode = deliveryReceipt.getError();
                }
            } catch (InvalidDeliveryReceiptException e) {
                LOGGER.error(e.getMessage(), e);
                descriptionError = e.getMessage();
            }
        }

    }

    protected VFResponse() {
        super(null, true);
    }

    public static VFResponse futureResponse() {
        return new VFResponse();
    }

    public static VFResponse failResponse(final String message) {
        final VFResponse failVFResponse = new VFResponse(null, new BasicResponse() {
            @Override
            public int getStatusCode() {
                return HttpServletResponse.SC_OK;
            }

            @Override
            public String getMessage() {
                return message;
            }
        });
        return failVFResponse;
    }

    @Override
    public String toString() {
        return "VFResponse{" +
               "phoneNumber='" + phoneNumber + '\'' +
               "} " + super.toString();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public VFResponse parse(DeliverSm receipt) {
        final BasicResponse response = new BasicResponse();
        final int statusCode = HttpServletResponse.SC_OK;
        final String message;

        if (receipt == null) {
            message = null;
        } else {
            message = receipt.toString();
        }

        response.setStatusCode(statusCode);
        response.setMessage(message);

        VFResponse vfResponse = new VFResponse(receipt, response);
        return vfResponse;
    }
}
