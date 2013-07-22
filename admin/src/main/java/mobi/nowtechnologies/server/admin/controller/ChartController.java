package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.validator.ChartDtoValidator;
import mobi.nowtechnologies.server.admin.validator.ChartItemDtoValidator;
import mobi.nowtechnologies.server.assembler.ChartAsm;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class ChartController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

	private ChartService chartService;
	private ChartItemController chartItemController;
	private String filesURL;
	private String chartFilesURL;
	private Map<String, String> viewByChartType;

	public void setViewByChartType(Map<String, String> viewByChartType) {
		this.viewByChartType = viewByChartType;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	public void setChartItemController(ChartItemController chartItemController) {
		this.chartItemController = chartItemController;
	}

	public void setFilesURL(String filesURL) {
		this.filesURL = filesURL;
	}
	
	public void setChartFilesURL(String chartFilesURL) {
		this.chartFilesURL = chartFilesURL;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateTimeFormat, true));
	}

	@InitBinder( { ChartItemDto.CHART_ITEM_DTO})
	public void initChartItemBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "channel", new StringTrimmerEditor(" ",true));
		binder.setValidator(new ChartItemDtoValidator());
	}

	@InitBinder( {ChartDto.CHART_DTO })
	public void initChartBinder(WebDataBinder binder) {
		ChartDtoValidator chartDtoValidator = new ChartDtoValidator();
		chartDtoValidator.setChartService(chartService);
		binder.setValidator(chartDtoValidator);	
	}

	@RequestMapping(value = "/charts/list", method = RequestMethod.GET)
	public ModelAndView getCharts(HttpServletRequest request) {
        LOGGER.debug("input parameters request [{}]", new Object[]{request});

        String communityURL = RequestUtils.getCommunityURL();
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, null);
        List<ChartDto> chartDtos = ChartAsm.toChartDtos(charts);

        return new ModelAndView("charts/charts")
                .addObject(ChartDto.CHART_DTO_LIST, chartDtos);
    }

    @RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
	public ModelAndView updateChart(
			@PathVariable("chartId") Integer chartId,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
			@Valid @ModelAttribute(ChartDto.CHART_DTO) ChartDto chartDto,
			BindingResult bindingResult,
			Locale locale
			) {

		LOGGER.debug("input parameters chartDto, chartId: [{}], [{}], [{}]", new Object[] { chartDto, chartId});

		ModelAndView modelAndView;
		
		if(!bindingResult.hasErrors()){			
			ChartDetail chartDetail = ChartAsm.toChart(chartDto);
			chartDetail.setPublishTimeMillis(selectedPublishDateTime.getTime());
			chartService.updateChart(chartDetail, chartDto.getFile());
			modelAndView = new ModelAndView("redirect:/charts/" + chartId);
		}else{
			modelAndView = chartItemController.getChartItemsPage(selectedPublishDateTime, chartId, false, locale);
			modelAndView.getModel().put("chart", chartDto);
		}

		return modelAndView;
	}

	/**
	 * Getting calendar look for picking up a date and list of chart items for
	 * current date
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}", method = RequestMethod.GET)
	public ModelAndView getChartCalendarPage(
			HttpServletRequest request,
			@RequestParam(required = false, value = "selectedPublishDateTime", defaultValue = "") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
			@PathVariable("chartId") Integer chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request, selectedPublishDateTime, chartId });

		if (selectedPublishDateTime == null)
			selectedPublishDateTime = new Date();

		Chart chart = chartService.getChartById(chartId);
		ChartDetail chartDetail = chartService.getChartDetails(Collections.singletonList(chart), selectedPublishDateTime, false).get(0);
		List<ChartDetail> chartItems = chartService.getActualChartItems(chartId, selectedPublishDateTime);
		List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(chartItems);
		ChartDto chartDto = ChartAsm.toChartDto(chartDetail);

		final String selectedPublishDateString;
		if (chartItemDtos.isEmpty()) {
			selectedPublishDateString = dateTimeFormat.format(selectedPublishDateTime);
		} else {
			selectedPublishDateString = dateTimeFormat.format(chartItemDtos.get(0).getPublishTime());
		}

		ModelAndView modelAndView = new ModelAndView(viewByChartType.get(chart.getType().name()));
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		modelAndView.addObject("selectedPublishDateTime", selectedPublishDateString);
		modelAndView.addObject("selectedDateTime", selectedPublishDateTime);
		modelAndView.addObject("allPublishTimeMillis", chartService.getAllPublishTimeMillis(chartId));
		modelAndView.addObject("filesURL", filesURL);
		modelAndView.addObject("chartFilesURL", chartFilesURL);
		modelAndView.addObject("chart", chartDto);

		return modelAndView;
	}
	
	/**
	 * Delete chart items for selected date
	 * 
	 * @param request
	 * @param selectedPublishDateTime
	 *            - selected date and time for which we delete chart items
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.DELETE)
	public ModelAndView deleteChartItems(HttpServletRequest request,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Integer chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request,
				selectedPublishDateTime, chartId });

		chartService.deleteChartItems(chartId, selectedPublishDateTime);
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId);
		return modelAndView;
	}
}
