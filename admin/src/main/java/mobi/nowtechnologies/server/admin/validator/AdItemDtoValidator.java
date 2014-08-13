package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.dto.AdItemDto;
import mobi.nowtechnologies.server.service.util.BaseValidator;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class AdItemDtoValidator extends BaseValidator {

    private static final int MAX_FILE_SIZE = 30720;
    private static final int MIN_FILE_SIZE = 1;

    @Override
    protected boolean customValidate(Object target, Errors errors) {

        AdItemDto adItemDto = (AdItemDto) target;

        MultipartFile file = adItemDto.getFile();

        validateFilePresence(errors, file, adItemDto.isRemoveImage());

        validateFileSize(errors, file);

        return errors.hasErrors();
    }

    private void validateFileSize(Errors errors, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() < MIN_FILE_SIZE || file.getSize() > MAX_FILE_SIZE) {
                errors.rejectValue("file", "ad.wrongFileSize.error", "Wrong file size. Should be more than 1 and less than 30720 bytes (30.72 kBytes)");
            }
        }
    }

    private void validateFilePresence(Errors errors, MultipartFile file, boolean removeImage) {
        if ((file == null || file.isEmpty()) && !removeImage) {
            errors.rejectValue("file", "ad.noFile.error", "No file is uploaded but \"None image\" is unchecked. Please check \"None image\" if you intentionally want to skip image upload.");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AdItemDto.class.isAssignableFrom(clazz);
    }

}
