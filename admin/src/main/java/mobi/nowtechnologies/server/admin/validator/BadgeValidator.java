package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.dto.streamzine.badge.ResolutionDto;
import mobi.nowtechnologies.server.service.util.BaseValidator;

import java.util.List;

import org.springframework.validation.Errors;

public class BadgeValidator extends BaseValidator {

    private List<Class<?>> types;

    public BadgeValidator(List<Class<?>> types) {
        this.types = types;
    }

    @Override
    protected boolean customValidate(Object target, Errors errors) {
        if (ResolutionDto.class.equals(target.getClass())) {
            ResolutionDto dto = (ResolutionDto) target;

            if (!DeviceType.ALL_DEVICE_TYPES.contains(dto.getDeviceType())) {
                errors.rejectValue("deviceType", "error.not.valid");
            }
        }

        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return types.contains(clazz);
    }
}
