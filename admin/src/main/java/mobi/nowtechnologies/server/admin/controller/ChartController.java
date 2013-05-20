package mobi.nowtechnologies.server.admin.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import mobi.nowtechnologies.server.admin.validator.ChartDtoValidator;
import mobi.nowtechnologies.server.admin.validator.ChartItemDtoValidator;
import mobi.nowtechnologies.server.assembler.ChartAsm;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.assembler.MediaAsm;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.shared.dto.admin.*;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class ChartController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

	private ChartService chartService;
	private String filesURL;
	private String chartFilesURL;
	private Map<String, String> viewByChartType;

	public void setViewByChartType(Map<String, String> viewByChartType) {
		this.viewByChartType = viewByChartType;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
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
		binder.setValidator(new ChartDtoValidator());	
	}
	
	@ModelAttribute("chartFilesURL")
	public String getFilesURL() {
		return chartFilesURL;
	}

	@RequestMapping(value = "/charts/list", method = RequestMethod.GET)
	public ModelAndView getCharts(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", new Object[] { request });

		String communityURL = RequestUtils.getCommunityURL();
		List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null);
		List<ChartDto> chartDtos = ChartAsm.toChartDtos(charts);

		ModelAndView modelAndView = new ModelAndView("charts/charts");
		modelAndView.addObject(ChartDto.CHART_DTO_LIST, chartDtos);

		return modelAndView;
	}
	
	/**Update properties of selected chart
	 * 
	 * @param chartItemDto dto of chart
	 * @param chartId id of chart
	 * @return redirect to the chart calender page
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
	public ModelAndView updateChart(
			@Valid @ModelAttribute(ChartDto.CHART_DTO) ChartDto chartDto,
			@PathVariable("chartId") Byte chartId,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime) {

		LOGGER.debug("input parameters chartDto, chartId: [{}], [{}], [{}]", new Object[] { chartDto, chartId});

		ChartDetail chartDetail = ChartAsm.toChart(chartDto);
		chartDetail.setPublishTimeMillis(selectedPublishDateTime.getTime());
		chartService.updateChart(chartDetail, chartDto.getFile());

		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId);

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
			@PathVariable("chartId") Byte chartId) {

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
		modelAndView.addObject("chart", chartDto);

		return modelAndView;
	}

	/**
	 * Getting chart items for selected date
	 * 
	 * @param request
	 * @param selectedPublishDateTime
	 *            - selected date and time represented in URI
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.GET)
	public ModelAndView getChartItemsPositionPage(HttpServletRequest request,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request, selectedPublishDateTime, chartId });

		List<ChartDetail> chartDetails = chartService.getChartItemsByDate(chartId, selectedPublishDateTime);
		List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(chartDetails);

		ModelAndView modelAndView = new ModelAndView("chartItems/chartItems");
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		modelAndView.addObject("selectedPublishDateTime", selectedPublishDateTime);
		modelAndView.addObject("filesURL", filesURL);
		modelAndView.addObject("chartId", chartId);

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
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request,
				selectedPublishDateTime, chartId });

		chartService.deleteChartItems(chartId, selectedPublishDateTime);
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId);
		return modelAndView;
	}

	/**
	 * 
	 * @param request
	 * @param selectedPublishDateTime
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/list", method = RequestMethod.POST)
	public ModelAndView cloneChartItemsForSelectedPublishDateIfOnesDoesNotExistForSelectedPublishDate(HttpServletRequest request,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request, selectedPublishDateTime, chartId });

		chartService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(selectedPublishDateTime, chartId);
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId + "/" + dateTimeFormat.format(selectedPublishDateTime));

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	/**
	 * Returning a page for creating a new chart item
	 * 
	 * @param request
	 * @param selectedPublishDateTime
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/new", method = RequestMethod.GET)
	public ModelAndView getNewChartItemPage(HttpServletRequest request,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
			@PathVariable("chartId") Byte chartId, @RequestParam(value = "q", required = false) String searchWords) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId, searchWords: [{}], [{}], [{}], [{}]", new Object[] { request,
				selectedPublishDateTime, chartId, searchWords });

		ModelAndView modelAndView = getNewChartItemModelAndView(selectedPublishDateTime, chartId, searchWords);
		return modelAndView;
	}

	@SuppressWarnings("unchecked")
	private ModelAndView getNewChartItemModelAndView(Date selectedPublishDateTime, Byte chartId, String searchWords) {
		final List<Media> medias;
		if (searchWords == null) {
			medias = Collections.EMPTY_LIST;
		} else
			medias = chartService.getMedias(searchWords);

		List<MediaDto> mediaDtos = MediaAsm.toMediaDtos(medias);

		ModelAndView modelAndView = new ModelAndView("chartItems/add");
		final ChartItemDto chartItemDto = new ChartItemDto();
		chartItemDto.setPublishTime(selectedPublishDateTime);
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO, chartItemDto);
		modelAndView.addObject(MediaDto.MEDIA_DTO_LIST, mediaDtos);
		modelAndView.addObject("selectedPublishDateTime", selectedPublishDateTime);
		modelAndView.addObject("filesURL", filesURL);
		modelAndView.addObject("chartId", chartId);
		return modelAndView;
	}

	/**
	 * Storing newly created chart item
	 * 
	 * @param request
	 * @param chartItemDto
	 * @param selectedPublishDateTime
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/new", method = RequestMethod.POST)
	public ModelAndView createNewChartItem(HttpServletRequest request, @Valid @ModelAttribute(ChartItemDto.CHART_ITEM_DTO) ChartItemDto chartItemDto,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, chartItemDto, selectedPublishDateTime, chartId: [{}], [{}], [{}], [{}]", new Object[] {
				request, chartItemDto, selectedPublishDateTime, chartId });

		chartItemDto.setChartId(chartId);
		chartItemDto.setPublishTime(selectedPublishDateTime);

		ModelAndView modelAndView;
		try {
			chartService.saveChartItem(chartItemDto);
			modelAndView = new ModelAndView("redirect:/charts/" + chartId + "/" + dateTimeFormat.format(selectedPublishDateTime));
		} catch (ServiceCheckedException e) {
			LOGGER.warn(e.getMessage(), e);
			modelAndView = getNewChartItemModelAndView(selectedPublishDateTime, chartId, null);
			modelAndView.addObject("errorCode", e.getErrorCodeForMessageLocalization());
		}

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	/**
	 * Getting a page for editing specific chart item for selected date
	 * 
	 * @param request
	 * @param chartItemId
	 *            - chart item id that needs to be edited
	 * @param selectedPublishDateTime
	 *            - selected date and time
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/{chartItemId}", method = RequestMethod.GET)
	public ModelAndView getChartItemPage(HttpServletRequest request, @PathVariable("chartItemId") Integer chartItemId,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, chartItemId, selectedPublishDateTime, chartId: [{}], [{}], [{}], [{}]", new Object[] { request, chartItemId,
				selectedPublishDateTime, chartId });

		ChartDetail chartDetail = chartService.getChartItemById(chartItemId);
		ChartItemDto chartItemDto = ChartDetailsAsm.toChartItemDto(chartDetail);

		if (chartItemDto == null)
			throw new ResourceNotFoundException("Can't find chart item for this id " + chartItemId);

		ModelAndView modelAndView = getChartItemModelAndView(selectedPublishDateTime, chartId, chartItemDto);

		return modelAndView;
	}

	private ModelAndView getChartItemModelAndView(Date selectedPublishDateTime, Byte chartId, ChartItemDto chartItemDto) {
		List<String> allChannels = chartService.getAllChannels();

		chartItemDto.setPublishTime(selectedPublishDateTime);

		ModelAndView modelAndView = new ModelAndView("chartItems/edit");
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO, chartItemDto);
		modelAndView.addObject("selectedPublishDateTime", selectedPublishDateTime);
		modelAndView.addObject("allChannels", allChannels);
		modelAndView.addObject("chartId", chartId);

		return modelAndView;
	}

	/**
	 * Update particular chart item according to selected date and chart item id
	 * 
	 * @param request
	 * @param chartItemDto
	 *            - chart item to be updated
	 * @param bindingResult
	 *            - result of binding firm inputs to java objects and validation
	 *            results
	 * @param selectedPublishDateTime
	 *            - selected date and time
	 * @param chartItemId
	 *            - chart item id
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/{chartItemId}", method = RequestMethod.POST)
	public ModelAndView updateChartItem(HttpServletRequest request, @Valid @ModelAttribute(ChartItemDto.CHART_ITEM_DTO) ChartItemDto chartItemDto,
			BindingResult bindingResult, @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
			@PathVariable("chartItemId") Integer chartItemId, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, chartItemDto, bindingResult, selectedPublishDateTime, chartItemId, chartId: [{}], [{}], [{}], [{}], [{}], [{}]",
				new Object[] { request, chartItemDto, bindingResult, selectedPublishDateTime, chartItemId, chartId });

		ModelAndView modelAndView = new ModelAndView("chartItem/edit");
		if (!bindingResult.hasErrors()) {
			chartService.updateChartItem(chartItemDto);
			modelAndView = new ModelAndView("redirect:/charts/" + chartId + "/" + dateTimeFormat.format(selectedPublishDateTime));
		} else {
			modelAndView = getChartItemModelAndView(selectedPublishDateTime, chartId, chartItemDto);
		}
		return modelAndView;
	}

	/**
	 * Deleting chart item by id according to selected date
	 * 
	 * @param request
	 * @param chartItemId
	 *            - id of the chart item
	 * @param selectedPublishDateTime
	 *            - selected date and time
	 * @return - returns the user to a list of the chart items for selected date
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/{chartItemId}", method = RequestMethod.DELETE)
	public ModelAndView deleteChartItem(HttpServletRequest request, @PathVariable("chartItemId") Integer chartItemId,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, chartItemId, selectedPublishDate, chartId: [{}], [{}], [{}], [{}]", new Object[] { request, chartItemId,
				selectedPublishDateTime, chartId });

		chartService.delete(chartItemId);
		
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId + "/" + dateTimeFormat.format(selectedPublishDateTime));
		return modelAndView;
	}
	
	/**
	 * 
	 * @param request
	 * @param selectedPublishDateTime
	 * @return
	 */
	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}/minorUpdate", method = RequestMethod.POST)
	public ModelAndView minorUpdateIfOnesDoesNotExistForSelectedPublishDate(HttpServletRequest request,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request, selectedPublishDateTime, chartId: [{}], [{}], [{}]", new Object[] { request, selectedPublishDateTime, chartId });

		chartService.minorUpdateIfOnesDoesNotExistForSelectedPublishDate(selectedPublishDateTime, chartId);
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId + "/" + dateTimeFormat.format(selectedPublishDateTime));

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
}
