package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ChartItemDtoValidator extends BaseValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartItemDtoValidator.class);

	@Override
	public boolean customValidate(Object target, Errors errors) {
		
		ChartItemDto chartItemDto = (ChartItemDto) target;
		
		String channel=chartItemDto.getChannel();
		if(channel!=null){
			if (channel.trim().isEmpty()) errors.rejectValue("channel", "chartItems.channel.isEmptyOrBlank.error", "Channel can be null but not empty string");
		}
		
		byte prevPosition = chartItemDto.getPrevPosition();
		if(prevPosition<0)
			errors.rejectValue("prevPosition", "chartItems.prevPosition.isLessThanZero.error", "Previous position cann't be less than zero");
		
		boolean hasErrors = errors.hasErrors();
		LOGGER.info("Output parameter errors=[{}]", errors);
		return hasErrors;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		final boolean supports = ChartItemDto.class.isAssignableFrom(clazz);
		return supports;
	}

}
