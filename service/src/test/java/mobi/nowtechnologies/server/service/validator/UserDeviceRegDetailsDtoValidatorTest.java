package mobi.nowtechnologies.server.service.validator;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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
        validator = new UserDeviceRegDetailsDtoValidator(request, userService, communityService);
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
