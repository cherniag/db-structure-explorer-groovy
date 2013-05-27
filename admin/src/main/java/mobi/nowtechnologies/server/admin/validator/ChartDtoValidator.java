package mobi.nowtechnologies.server.admin.validator;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.springframework.validation.Errors;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ChartDtoValidator extends BaseValidator {
	
	private ChartService chartService;

	@Override
	public boolean customValidate(Object target, Errors errors) {
		ChartDto chartDto = (ChartDto) target;
		
		if(ChartType.FOURTH_CHART.equals(chartDto.getChartType()) && chartDto.getPosition() != 0){
			errors.rejectValue("position", "chart.position.error.invalidPositionForFourthChart", "The position for Fourth playlist should be only zero");
		}
		
		String communityURL = RequestUtils.getCommunityURL();
		List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, null);
		for(ChartDetail chartDetail : charts){
			if(!chartDetail.getChart().getI().equals(chartDto.getId()) && chartDetail.getPosition() == chartDto.getPosition().byteValue()){
				errors.rejectValue("position", "chart.position.error.samePositionExists", "The chart '"+chartDetail.getTitle()+"' already has position "+chartDto.getPosition());
				break;
			}
		}
		
		
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
