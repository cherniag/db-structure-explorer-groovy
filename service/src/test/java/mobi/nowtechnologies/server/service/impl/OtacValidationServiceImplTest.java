package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.impl.details.O2ProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.impl.details.VfNzProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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

    @Mock
    public CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @Mock
    public ApplicationContext applicationContext;

    public O2ProviderDetailsExtractor o2ProviderDetailsExtractor = new O2ProviderDetailsExtractor();

    public VfNzProviderDetailsExtractor vfNzProviderDetailsExtractor = new VfNzProviderDetailsExtractor();

    @Before
    public void setUp(){
        otacValidationServiceImplFixture = new OtacValidationServiceImpl();
        ReflectionTestUtils.setField(otacValidationServiceImplFixture, "messageSource", communityResourceBundleMessageSource);
        ReflectionTestUtils.setField(otacValidationServiceImplFixture, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(o2ProviderDetailsExtractor, "o2ProviderService", o2ProviderServiceMock);
        ReflectionTestUtils.setField(vfNzProviderDetailsExtractor, "vfOtacValidationService", vfOtacValidationServiceMock);
    }

    @Test
    public void shouldUseO2ProviderService(){
        //given
        String otac = "otac";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("o2").withName("o2");

        ProviderUserDetails expectedProviderUserDetails = new ProviderUserDetails();

        doReturn(expectedProviderUserDetails).when(o2ProviderServiceMock).getUserDetails(otac, phoneNumber, community);
        doReturn("o2ProviderDetailsExtractor").when(communityResourceBundleMessageSource).getMessage("o2", "providerDetailsExtractor.beanName", null, null);
        doReturn(o2ProviderDetailsExtractor).when(applicationContext).getBean("o2ProviderDetailsExtractor");
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
        Community community = new Community().withRewriteUrl("vf_nz").withName("vf_nz");

        ProviderUserDetails expectedProviderUserDetails = new ProviderUserDetails();

        doReturn(null).when(o2ProviderServiceMock).getUserDetails(otac, phoneNumber, community);
        doReturn("vfnzProviderDetailsExtractor").when(communityResourceBundleMessageSource).getMessage("vf_nz", "providerDetailsExtractor.beanName", null, null);
        doReturn(vfNzProviderDetailsExtractor).when(applicationContext).getBean("vfnzProviderDetailsExtractor");
        doReturn(expectedProviderUserDetails).when(vfOtacValidationServiceMock).validate(otac, phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = otacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails, is(expectedProviderUserDetails));

        verify(o2ProviderServiceMock, times(0)).getUserDetails(otac, phoneNumber, community);
        verify(vfOtacValidationServiceMock, times(1)).validate(otac, phoneNumber, community);
    }

}
