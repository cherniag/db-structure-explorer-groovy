package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;

import org.springframework.validation.Errors;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ChartDtoValidator extends BaseValidator {
	
	private ChartService chartService;

	@Override
	public boolean customValidate(Object target, Errors errors) {
		return errors.hasErrors();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ChartDto.class.isAssignableFrom(clazz);
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}
}
