package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

/**
 * User: Titov Mykhaylo (titov)
 * 30.09.13 17:20
 */
@RunWith(PowerMockRunner.class)
public class OtacValidationServiceImplTest {

    private OtacValidationServiceImpl otacValidationServiceImplFixture;

    @Mock
    public O2ProviderService o2ProviderServiceMock;

    @Mock
    public VFOtacValidationService vfOtacValidationServiceMock;

    @Before
    public void setUp(){
        otacValidationServiceImplFixture = new OtacValidationServiceImpl();

        otacValidationServiceImplFixture.setO2ProviderService(o2ProviderServiceMock);
        otacValidationServiceImplFixture.setVfOtacValidationService(vfOtacValidationServiceMock);
    }

    @Test
    public void shouldUseO2ProviderService(){
        //given
        String otac = "otac";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("o2");

        ProviderUserDetails expectedProviderUserDetails = new ProviderUserDetails();

        doReturn(expectedProviderUserDetails).when(o2ProviderServiceMock).getUserDetails(otac, phoneNumber, community);
        doReturn(null).when(vfOtacValidationServiceMock).validate(otac, phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = otacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails, is(expectedProviderUserDetails));

        verify(o2ProviderServiceMock, times(1)).getUserDetails(otac, phoneNumber, community);
        verify(vfOtacValidationServiceMock, times(0)).validate(otac, phoneNumber, community);
    }

    @Test
    public void shouldUseVFNZProviderService(){
        //given
        String otac = "otac";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        ProviderUserDetails expectedProviderUserDetails = new ProviderUserDetails();

        doReturn(null).when(o2ProviderServiceMock).getUserDetails(otac, phoneNumber, community);
        doReturn(expectedProviderUserDetails).when(vfOtacValidationServiceMock).validate(otac, phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = otacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails, is(expectedProviderUserDetails));

        verify(o2ProviderServiceMock, times(0)).getUserDetails(otac, phoneNumber, community);
        verify(vfOtacValidationServiceMock, times(1)).validate(otac, phoneNumber, community);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUseAnyProviderService(){
        //given
        String otac = "otac";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("unknown");

        ProviderUserDetails expectedProviderUserDetails = new ProviderUserDetails();

        doReturn(expectedProviderUserDetails).when(o2ProviderServiceMock).getUserDetails(otac, phoneNumber, community);
        doReturn(expectedProviderUserDetails).when(vfOtacValidationServiceMock).validate(otac, phoneNumber, community);

        //when
        otacValidationServiceImplFixture.validate(otac, phoneNumber, community);
    }
}
