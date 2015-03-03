package mobi.nowtechnologies.server.service.validator;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;

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
public class UserRegDetailsDtoValidatorTest {

    @Mock
    private UserService userService;


    private MockHttpServletRequest request = new MockHttpServletRequest();


    private UserRegDetailsDtoValidator validator;

    private UserRegDetailsDto dto;

    private Errors errors;

    @Before
    public void before() {
        validator = new UserRegDetailsDtoValidator(request, userService, null);
        dto = new UserRegDetailsDto();
        dto.setPassword("TEST");
        dto.setEmail("test@gmail.com");
        errors = new BeanPropertyBindingResult(dto, "");
    }

    @Test
    public void testCountryIsNotSupported() {
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(false);
        validator.customValidate(dto, errors);
        assertTrue(errors.hasErrors());
        assertEquals("We don't support your country", errors.getFieldError("ipAddress").getDefaultMessage());
    }

    @Test
    public void testCountryIsSupported() {
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(true);
        validator.customValidate(dto, errors);
        assertNull(errors.getFieldError("ipAddress"));
    }
}
