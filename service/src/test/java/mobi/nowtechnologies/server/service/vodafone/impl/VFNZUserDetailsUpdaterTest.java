package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import java.util.Arrays;

import org.jsmpp.bean.DeliverSm;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: Alexsandr_Kolpakov Date: 10/21/13 Time: 10:32 AM
 */
public class VFNZUserDetailsUpdaterTest {
    private VFNZUserDetailsUpdater fixture = new VFNZUserDetailsUpdater();

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fixture.setUserRepository(userRepository);
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
        User user1 = UserFactory.createUser(ActivationStatus.ACTIVATED);
        User user2 = UserFactory.createUser(ActivationStatus.ACTIVATED);

        VFNZSubscriberData data = new VFNZSubscriberData();
        data.setPhoneNumber("+642111111111");
        user1.setMobile(data.getPhoneNumber());
        user2.setMobile(data.getPhoneNumber());

        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user1, data);
        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user2, data);
        Mockito.doReturn(Arrays.asList(user1, user2)).when(userRepository).findByMobile(data.getPhoneNumber());

        fixture.process(data);

        verify(userServiceMock, times(1)).populateSubscriberData(user1, data);
        verify(userServiceMock, times(1)).populateSubscriberData(user2, data);
        verify(userRepository, times(1)).findByMobile(data.getPhoneNumber());
    }
}
