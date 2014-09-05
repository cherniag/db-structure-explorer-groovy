package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.streamzine.badge.BadgeInfoDto;
import mobi.nowtechnologies.server.dto.streamzine.badge.ResolutionDto;
import org.junit.Test;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BadgeValidatorTest {

    @Test
    public void testCustomValidateSuccess() throws Exception {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(ResolutionDto.class);

        Errors errors = mock(Errors.class);
        ResolutionDto target = mock(ResolutionDto.class);
        when(target.getDeviceType()).thenReturn(UserRegInfo.DeviceType.ANDROID);

        new BadgeValidator(types).customValidate(target, errors);

        verifyNoMoreInteractions(errors);
    }

    @Test
    public void testCustomValidateFailure() throws Exception {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(ResolutionDto.class);

        Errors errors = mock(Errors.class);

        ResolutionDto target = new ResolutionDto();
        target.setDeviceType("unknown-device-type");

        new BadgeValidator(types).customValidate(target, errors);

        verify(errors).rejectValue("deviceType", "error.not.valid");
    }

    @Test
    public void testSupports() throws Exception {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(BadgeInfoDto.class);
        assertTrue(new BadgeValidator(types).supports(BadgeInfoDto.class));
    }
}