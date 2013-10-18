package mobi.nowtechnologies.server.service.vodafone.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.validator.NZCellNumberValidator;
import mobi.nowtechnologies.server.shared.Processor;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class})
public class VFNZProviderServiceImplTest {
    private VFNZProviderServiceImpl fixture;

    @Mock
    private VFNZSMSGatewayServiceImpl gatewayService;

    @Mock
    private NZCellNumberValidator mockNzCellNumberValidator;

    @Mock
    private DeviceService mockDeviceService;

    @Before
    public void setUp() throws Exception {
          fixture = new VFNZProviderServiceImpl();
          fixture.setPhoneValidator(mockNzCellNumberValidator);
          fixture.setGatewayService(gatewayService);
          fixture.setDeviceService(mockDeviceService);
          fixture.setProviderNumber("5803");
    }

    @Test
    public void testValidatePhoneNumber_Success() throws Exception {
        String phoneNumber = "2111111111";

        Mockito.when(mockNzCellNumberValidator.validate(eq(phoneNumber))).thenReturn("+642111111111");
        Mockito.when(mockDeviceService.isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String)null))).thenReturn(false);
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(Utils.generateRandomPIN()).thenReturn(1000);

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        Assert.assertEquals("+642111111111", result.getPhoneNumber());
        Assert.assertEquals("1000", result.getPin());

        Mockito.verify(mockNzCellNumberValidator, Mockito.times(1)).validate(eq(phoneNumber));
        verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null));
        PowerMockito.verifyStatic(times(1));
        Utils.generateRandomPIN();
    }

    @Test
    public void testValidatePhoneNumber_PromotedPhone_Success() throws Exception {
        String phoneNumber = "2111111111";

        Mockito.when(mockNzCellNumberValidator.validate(eq(phoneNumber))).thenReturn("+642111111111");
        Mockito.when(mockDeviceService.isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String)null))).thenReturn(true);
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(Utils.generateRandomPIN()).thenReturn(1000);

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        Assert.assertEquals("2111111111", result.getPhoneNumber());
        Assert.assertEquals("1000", result.getPin());

        Mockito.verify(mockNzCellNumberValidator, Mockito.times(0)).validate(eq(phoneNumber));
        verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null));
        PowerMockito.verifyStatic(times(1));
        Utils.generateRandomPIN();
    }

    @Test
    public void testGetSubscriberData_Success() throws Exception {
        final String phoneNumber = "+642111111111";
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };

        Mockito.doReturn(null).when(gatewayService).send(eq(fixture.providerNumber), eq(phoneNumber), eq("GET_PROVIDER"));

        fixture.getSubscriberData(phoneNumber, processor);

        Mockito.verify(gatewayService, Mockito.times(1)).send(eq(phoneNumber), eq("GET_PROVIDER"), eq(fixture.providerNumber));
    }
}
