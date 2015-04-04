package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import static mobi.nowtechnologies.server.service.VFOtacValidationService.TEST_OTAC_NON_VF;
import static mobi.nowtechnologies.server.service.VFOtacValidationService.TEST_OTAC_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;

/**
 * User: Titov Mykhaylo (titov) 30.09.13 17:41
 */
public class VFOtacValidationServiceImplTest {

    @Mock
    public DevicePromotionsService deviceService;
    @Mock
    public UserRepository userRepository;
    @InjectMocks
    private VFOtacValidationServiceImpl vfOtacValidationServiceImplFixture;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldValidateAsPromotedNonVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(1L).when(userRepository).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        doReturn(true).when(deviceService).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.operator, is(NON_VF.getKey()));

        verify(userRepository, times(0)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        verify(deviceService, times(1)).isPromotedDevicePhone(community, phoneNumber, null);
    }

    @Test
    public void shouldValidateAsPromotedVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(1L).when(userRepository).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        doReturn(true).when(deviceService).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.operator, is(VF.getKey()));

        verify(userRepository, times(0)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        verify(deviceService, times(1)).isPromotedDevicePhone(community, phoneNumber, null);
    }

    @Test
    public void shouldValidateAsPromotedVFDeviceWithNotTestedOtac() throws Exception {
        //given
        String otac = "";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(1L).when(userRepository).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        doReturn(true).when(deviceService).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertNull(providerUserDetails.operator);

        verify(userRepository, times(1)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        verify(deviceService, times(1)).isPromotedDevicePhone(community, phoneNumber, null);
    }

    @Test
    public void shouldValidateAsNotPromotedVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(1L).when(userRepository).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        doReturn(false).when(deviceService).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertNull(providerUserDetails.operator);

        verify(userRepository, times(1)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
        verify(deviceService, times(1)).isPromotedDevicePhone(community, phoneNumber, null);
    }
}
