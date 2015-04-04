package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.validator.NZCellNumberValidator;
import mobi.nowtechnologies.server.shared.Processor;
import mobi.nowtechnologies.server.shared.Utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created with IntelliJ IDEA. User: Alexsandr_Kolpakov Date: 10/2/13 Time: 3:47 PM To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class})
public class VFNZProviderServiceImplTest {

    private VFNZProviderServiceImpl fixture;

    @Mock
    private VFNZSMSGatewayServiceImpl gatewayService;

    @Mock
    private NZCellNumberValidator mockNzCellNumberValidator;

    @Mock
    private DevicePromotionsService mockDeviceService;

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

        Mockito.when(mockNzCellNumberValidator.validateAndNormalize(eq(phoneNumber))).thenReturn("+642111111111");
        Mockito.when(mockDeviceService.isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null))).thenReturn(false);
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(Utils.generateRandom4DigitsPIN()).thenReturn("1000");

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        Assert.assertEquals("+642111111111", result.getPhoneNumber());
        Assert.assertEquals("1000", result.getPin());

        Mockito.verify(mockNzCellNumberValidator, Mockito.times(1)).validateAndNormalize(eq(phoneNumber));
        verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null));
        PowerMockito.verifyStatic(times(1));
        Utils.generateRandom4DigitsPIN();
    }

    @Test
    public void testValidatePhoneNumber_PromotedPhone_Success() throws Exception {
        String phoneNumber = "2111111111";

        Mockito.when(mockNzCellNumberValidator.validateAndNormalize(eq(phoneNumber))).thenReturn("+642111111111");
        Mockito.when(mockDeviceService.isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null))).thenReturn(true);
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(Utils.generateRandom4DigitsPIN()).thenReturn("1000");

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        Assert.assertEquals("2111111111", result.getPhoneNumber());
        Assert.assertEquals("1000", result.getPin());

        Mockito.verify(mockNzCellNumberValidator, Mockito.times(0)).validateAndNormalize(eq(phoneNumber));
        verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null));
        PowerMockito.verifyStatic(times(1));
        Utils.generateRandom4DigitsPIN();
    }

    @Test
    public void testGetSubscriberData_Success() throws Exception {
        final String phoneNumber = "+642111111111";
        final Processor processor = spy(new Processor() {
            @Override
            public void process(Object data) {
                VFNZSubscriberData subscriberData = (VFNZSubscriberData) data;
                Assert.assertEquals(null, subscriberData.getProvider());
                Assert.assertEquals(phoneNumber, subscriberData.getPhoneNumber());
                fixture.LOGGER.info("process msg");
            }
        });

        Mockito.doReturn(null).when(gatewayService).send(eq(fixture.providerNumber), eq(phoneNumber), eq("GET_PROVIDER"));

        fixture.getSubscriberData(phoneNumber, processor);

        Mockito.verify(gatewayService, Mockito.times(1)).send(eq(phoneNumber), eq("GET_PROVIDER"), eq(fixture.providerNumber));
        Mockito.verify(processor, Mockito.times(1)).process(any(VFNZSubscriberData.class));
    }

    @Test
    public void testValidatePhoneNumberPinCodeNotStartsWith7Constantly() {
        String phoneNumber = "+642102247311";
        Mockito.when(mockDeviceService.isPromotedDevicePhone(any(Community.class), eq(phoneNumber), eq((String) null))).thenReturn(true);

        int num = 10;
        List<String> codes = new ArrayList<String>(num);
        for (int i = 0; i < num; i++) {
            PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);
            codes.add(result.getPin());
        }

        int numOfCodesStartWith7 = 0;
        for (String code : codes) {
            if (code.startsWith("7")) {
                numOfCodesStartWith7++;
            }
        }
        assertFalse("All codes start with '7'", num == numOfCodesStartWith7);
    }

}
