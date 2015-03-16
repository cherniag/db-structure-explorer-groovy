package mobi.nowtechnologies.server.service.payment;


import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MTVNZPaymentResponseParserTest {
    private MTVNZPaymentResponseParser parser = new MTVNZPaymentResponseParser();

    @Test
    public void parseDELIVRD_Success() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn("64123456789");
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.DELIVRD);

        final MTVNZResponse response = parser.parse(deliverSm);

        assertTrue(response.isSuccessful());
        assertEquals("+" + deliverSm.getSourceAddr(), response.getPhoneNumber());
        assertEquals(null, response.getDescriptionError());
        assertEquals(null, response.getErrorCode());
        assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        assertFalse(response.isFuture());
    }

    @Test
    public void parseACCEPTD_Success() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn("64123456789");
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.ACCEPTD);

        final MTVNZResponse response = parser.parse(deliverSm);

        assertTrue(response.isSuccessful());
        assertEquals("+" + deliverSm.getSourceAddr(), response.getPhoneNumber());
        assertEquals(null, response.getDescriptionError());
        assertEquals(null, response.getErrorCode());
        assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        assertFalse(response.isFuture());
    }

    @Test
    public void parseUNDELIV_Success() throws Exception {
        final String errorCode = "001";

        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn("64123456789");
        DeliveryReceipt deliveryReceipt = mock(DeliveryReceipt.class);
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenReturn(deliveryReceipt);
        when(deliveryReceipt.getFinalStatus()).thenReturn(DeliveryReceiptState.UNDELIV);
        when(deliveryReceipt.getError()).thenReturn(errorCode);

        final MTVNZResponse response = parser.parse(deliverSm);

        assertFalse(response.isSuccessful());
        assertEquals("+" + deliverSm.getSourceAddr(), response.getPhoneNumber());
        assertEquals("UNDELIV", response.getDescriptionError());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        assertFalse(response.isFuture());
    }

    @Test
    public void parseWhenException() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.getSourceAddr()).thenReturn("64123456789");
        when(deliverSm.getShortMessageAsDeliveryReceipt()).thenThrow(new InvalidDeliveryReceiptException("Invalid Delivery Receipt Exception"));

        final MTVNZResponse response = parser.parse(deliverSm);

        assertFalse(response.isSuccessful());
        assertEquals("+" + deliverSm.getSourceAddr(), response.getPhoneNumber());
        assertEquals("Invalid Delivery Receipt Exception", response.getDescriptionError());
        assertEquals(null, response.getErrorCode());
        assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        assertFalse(response.isFuture());
    }

}