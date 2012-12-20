package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.dto.AdItemDto;
import mobi.nowtechnologies.server.service.util.BaseValidator;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AdItemDtoValidator extends BaseValidator {

	@Override
	public boolean customValidate(Object target, Errors errors) {

		AdItemDto adItemDto = (AdItemDto) target;

		MultipartFile file = adItemDto.getFile();
		Integer id = adItemDto.getId();
		
		if (id != null && adItemDto.getImageFileName() == null) {
			errors.rejectValue("imageFileName", "ad.imageFileNameFieldIsNull.error", "The field imageFileName is mandatory");
		}
		
		if (id == null || file != null) {
			if (id == null && file == null) {
				errors.rejectValue("file", "ad.fileFieldIsNull.error", "The field file is mandatory");
			} else {

				long size = file.getSize();
				if (id == null && (size < 1 || size > 30720)) {
					errors.rejectValue("file", "ad.wrongFileSize.error", "Wrong file size. Should be more than 1 and less than 30720 bytes (30.72 kBytes)");
				}
			}
		}

		boolean hasErrors = errors.hasErrors();
		return hasErrors;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		final boolean supports = AdItemDto.class.isAssignableFrom(clazz);
		return supports;
	}

}
