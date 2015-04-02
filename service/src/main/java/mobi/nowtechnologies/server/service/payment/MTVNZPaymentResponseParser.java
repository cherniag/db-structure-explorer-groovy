package mobi.nowtechnologies.server.service.payment;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/27/2015
 */
public class MTVNZPaymentResponseParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MTVNZPaymentResponseParser.class);

    public MTVNZResponse parse(DeliverSm deliverSm) {
        String phoneNumber = null;
        String errorCode = null;
        String descriptionError;

        try {
            phoneNumber = "+" + deliverSm.getSourceAddr();
            DeliveryReceipt deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

            if (isSuccessful(deliveryReceipt)) {
                return MTVNZResponse.successfulResponse(phoneNumber);
            }

            descriptionError = deliveryReceipt.getFinalStatus().toString();
            errorCode = deliveryReceipt.getError();
        } catch (InvalidDeliveryReceiptException e) {
            LOGGER.error(e.getMessage(), e);
            descriptionError = e.getMessage();
        }
        return MTVNZResponse.errorResponse(phoneNumber, errorCode, descriptionError);
    }

    private boolean isSuccessful(DeliveryReceipt deliveryReceipt) {
        return deliveryReceipt.getFinalStatus() == DeliveryReceiptState.ACCEPTD || deliveryReceipt.getFinalStatus() == DeliveryReceiptState.DELIVRD;
    }
}
