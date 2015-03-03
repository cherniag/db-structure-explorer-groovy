package mobi.nowtechnologies.server.service.validator;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class UserDeviceRegDetailsDtoValidatorTest {

    @Mock
    private UserService userService;

    @Mock
    private CommunityService communityService;

    private MockHttpServletRequest request = new MockHttpServletRequest();


    private UserDeviceRegDetailsDtoValidator validator;

    private UserDeviceRegDetailsDto dto;

    private Errors errors;

    @Before
    public void before() {
        request.setRequestURI("/o2/3.9/SING_UP_DEVICE");
        validator = new UserDeviceRegDetailsDtoValidator("o2", "", userService, communityService);
        dto = new UserDeviceRegDetailsDto();
        request.setRequestURI("o2/transport/service");
        errors = new BeanPropertyBindingResult(dto, "");
    }

    @Test
    public void testCountryIsNotSupported() {
        Community community = new Community();
        when(communityService.getCommunityByUrl(anyString())).thenReturn(community);
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(false);
        validator.customValidate(dto, errors);
        assertTrue(errors.hasErrors());
        assertEquals("We don't support your country", errors.getFieldError("ipAddress").getDefaultMessage());
    }

    @Test
    public void testCountryIsSupported() {
        Community community = new Community();
        when(communityService.getCommunityByUrl(anyString())).thenReturn(community);
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(true);
        validator.customValidate(dto, errors);
        assertNull(errors.getFieldError("ipAddress"));
    }

}
