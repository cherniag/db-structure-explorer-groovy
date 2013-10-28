package mobi.nowtechnologies.server.service.payment.response;

import junit.framework.Assert;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletResponse;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/22/13
 * Time: 10:54 AM
 */
@RunWith(PowerMockRunner.class)
public class VFResponseTest {
    private VFResponse fixture;

    @Before
    public void setUp() throws Exception {
        fixture = VFResponse.futureResponse();
    }

    @Test
    public void testParse_Successful_DELIVRD_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "DELIVRD",  "000", "It is test").getBytes());

        VFResponse vfResponse = fixture.parse(deliverSm);

        Assert.assertEquals(true, vfResponse.isSuccessful());
        Assert.assertEquals(false, vfResponse.isFuture());
        Assert.assertEquals("+"+deliverSm.getSourceAddr(), vfResponse.getPhoneNumber());
        Assert.assertEquals("PDUHeader(0, 00000000, 00000000, 0)", vfResponse.getMessage());
        Assert.assertEquals("", vfResponse.getDescriptionError());
        Assert.assertEquals(null, vfResponse.getErrorCode());
        Assert.assertEquals(HttpServletResponse.SC_OK, vfResponse.getHttpStatus());
    }

    @Test
    public void testParse_Successful_ACCEPTD_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "ACCEPTD",  "000", "It is test").getBytes());

        VFResponse vfResponse = fixture.parse(deliverSm);

        Assert.assertEquals(true, vfResponse.isSuccessful());
        Assert.assertEquals(false, vfResponse.isFuture());
        Assert.assertEquals("+"+deliverSm.getSourceAddr(), vfResponse.getPhoneNumber());
        Assert.assertEquals("PDUHeader(0, 00000000, 00000000, 0)", vfResponse.getMessage());
        Assert.assertEquals("", vfResponse.getDescriptionError());
        Assert.assertEquals(null, vfResponse.getErrorCode());
        Assert.assertEquals(HttpServletResponse.SC_OK, vfResponse.getHttpStatus());
    }

    @Test
    public void testParse_UnSuccessful_UNDELIV_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "UNDELIV",  "001", "It is test").getBytes());

        VFResponse vfResponse = fixture.parse(deliverSm);

        Assert.assertEquals(false, vfResponse.isSuccessful());
        Assert.assertEquals(false, vfResponse.isFuture());
        Assert.assertEquals("+"+deliverSm.getSourceAddr(), vfResponse.getPhoneNumber());
        Assert.assertEquals("PDUHeader(0, 00000000, 00000000, 0)", vfResponse.getMessage());
        Assert.assertEquals("UNDELIV", vfResponse.getDescriptionError());
        Assert.assertEquals("001", vfResponse.getErrorCode());
        Assert.assertEquals(HttpServletResponse.SC_OK, vfResponse.getHttpStatus());
    }

    @Test
    public void testParse_UnSuccessful_NotDeliveryReceipt_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "UNDELIV",  "001", "It is test").getBytes());

        VFResponse vfResponse = fixture.parse(deliverSm);

        Assert.assertEquals(false, vfResponse.isSuccessful());
        Assert.assertEquals(false, vfResponse.isFuture());
        Assert.assertEquals("+"+deliverSm.getSourceAddr(), vfResponse.getPhoneNumber());
        Assert.assertEquals("PDUHeader(0, 00000000, 00000000, 0)", vfResponse.getMessage());
        Assert.assertEquals("deliver_sm is not a Delivery Receipt since ems_class value = 0", vfResponse.getDescriptionError());
        Assert.assertEquals(null, vfResponse.getErrorCode());
        Assert.assertEquals(HttpServletResponse.SC_OK, vfResponse.getHttpStatus());
    }

    @Test
    public void testParse_UnSuccessful_InvalidDeliveryReceipt_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "aaaaaaaa", "1310020119", "UNDELIV",  "001", "It is test").getBytes());

        VFResponse vfResponse = fixture.parse(deliverSm);

        Assert.assertEquals(false, vfResponse.isSuccessful());
        Assert.assertEquals(false, vfResponse.isFuture());
        Assert.assertEquals("+"+deliverSm.getSourceAddr(), vfResponse.getPhoneNumber());
        Assert.assertEquals("PDUHeader(0, 00000000, 00000000, 0)", vfResponse.getMessage());
        Assert.assertEquals("There is an error found when parsing delivery receipt", vfResponse.getDescriptionError());
        Assert.assertEquals(null, vfResponse.getErrorCode());
        Assert.assertEquals(HttpServletResponse.SC_OK, vfResponse.getHttpStatus());
    }

    protected String buildMessage(String msgId, String sub, String dlvrd,String submitDate, String doneDate, String stat, String err, String text){
        return "id:"+msgId+" sub:"+sub+" dlvrd:"+dlvrd+" submit date:"+submitDate+" done date:"+doneDate+" stat:"+stat+" err:"+err+" Text:"+text;
    }
}
