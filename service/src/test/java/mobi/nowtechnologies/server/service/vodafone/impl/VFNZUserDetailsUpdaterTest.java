package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.UserService;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/21/13
 * Time: 10:32 AM
 */
@RunWith(PowerMockRunner.class)
public class VFNZUserDetailsUpdaterTest {

    private VFNZUserDetailsUpdater fixture;

    @Mock
    private UserService userServiceMock;

    @Before
    public void setUp() throws Exception {
        fixture = new VFNZUserDetailsUpdater();
        fixture.setUserService(userServiceMock);
    }

    @Test
    public void testSupports_Supported_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5803");
        fixture.setProviderNumber(deliverSm.getSourceAddr());

        boolean result = fixture.supports(deliverSm);

        assertEquals(true, result);
    }

    @Test
    public void testSupports_NotSupported_NotSupportedNumber_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5803");
        fixture.setProviderNumber("4003");

        boolean result = fixture.supports(deliverSm);

        assertEquals(false, result);
    }

    @Test
    public void testSupports_NotSupported_IsDeliverReceipt_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("5803");
        fixture.setProviderNumber(deliverSm.getSourceAddr());

        boolean result = fixture.supports(deliverSm);

        assertEquals(false, result);
    }

    @Test
    public void testProcess_Success() throws Exception {
        User user1 = UserFactory.createUser();
        User user2 = UserFactory.createUser();

        VFNZSubscriberData data = new VFNZSubscriberData();
        data.setPhoneNumber("+642111111111");
        user1.setMobile(data.getPhoneNumber());
        user2.setMobile(data.getPhoneNumber());

        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user1, data);
        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user2, data);
        Mockito.doReturn(Arrays.asList(user1, user2)).when(userServiceMock).findByMobile(data.getPhoneNumber());

        fixture.process(data);

        verify(userServiceMock, times(1)).populateSubscriberData(user1, data);
        verify(userServiceMock, times(1)).populateSubscriberData(user2, data);
        verify(userServiceMock, times(1)).findByMobile(data.getPhoneNumber());
    }
}
