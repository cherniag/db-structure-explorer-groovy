package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.validator.ChartDtoValidator;
import mobi.nowtechnologies.server.admin.validator.ChartItemDtoValidator;
import mobi.nowtechnologies.server.admin.asm.ChartAsm;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
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
import java.text.SimpleDateFormat;
import java.util.*;

// @author Titov Mykhaylo (titov)
@Controller
public class ChartController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

	private ChartService chartService;
	private ChartItemController chartItemController;
	private String filesURL;
	private String chartFilesURL;
	private Map<ChartType, String> viewByChartType;
    private CommunityRepository communityRepository;
    private ChartAsm chartAsm;

    @InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(URL_DATE_TIME_FORMAT), true));
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
        List<ChartDto> chartDTOs = chartAsm.toChartDtos(charts);

        return new ModelAndView("charts/charts")
                .addObject(ChartDto.CHART_DTO_LIST, chartDTOs);
    }

    @RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
	public ModelAndView updateChart(
                            @PathVariable("chartId") Integer chartId,
                            @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                            @Valid @ModelAttribute(ChartDto.CHART_DTO) ChartDto chartDto,
                            @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl,
                            BindingResult bindingResult,
                            Locale locale
                            ) {

        LOGGER.debug("input parameters chartDto, chartId: [{}], [{}], [{}]", new Object[] { chartDto, chartId});

		ModelAndView modelAndView;
		
		if(!bindingResult.hasErrors()){			
			ChartDetail chartDetail = chartAsm.toChart(chartDto);
			chartDetail.setPublishTimeMillis(selectedPublishDateTime.getTime());
			chartService.updateChart(chartDetail, chartDto.getFile());
			modelAndView = new ModelAndView("redirect:/charts/" + chartId);
		}else{
			modelAndView = chartItemController.getChartItemsPage(selectedPublishDateTime, chartId, false, locale);
			modelAndView.getModel().put("chart", chartDto);
		}

		return modelAndView;
	}

	@RequestMapping(value = "/charts/{chartId}", method = RequestMethod.GET)
	public ModelAndView getChartCalendarPage(
                            @RequestParam(required = false, value = "selectedPublishDateTime", defaultValue = "") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                            @PathVariable("chartId") Integer chartId) {

		LOGGER.debug("input parameters selectedPublishDateTime, chartId: [{}], [{}], [{}]", selectedPublishDateTime, chartId);

		if (selectedPublishDateTime == null) {
            selectedPublishDateTime = new Date();
        }

		Chart chart = chartService.getChartById(chartId);
		ChartDetail chartDetail = chartService.getChartDetails(Collections.singletonList(chart), selectedPublishDateTime, false).get(0);
		List<ChartDetail> chartItems = chartService.getActualChartItems(chartId, selectedPublishDateTime);
		List<ChartItemDto> chartItemDTOs = ChartDetailsAsm.toChartItemDtos(chartItems);
		ChartDto chartDto = chartAsm.toChartDto(chartDetail);

        return new ModelAndView(viewByChartType.get(chart.getType()))
                    .addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDTOs)
                    .addObject("selectedPublishDateTime", getSelectedPublishDateAsString(selectedPublishDateTime, chartItemDTOs))
                    .addObject("selectedDateTime", selectedPublishDateTime)
                    .addObject("allPublishTimeMillis", chartService.getAllPublishTimeMillis(chartId))
                    .addObject("filesURL", filesURL)
                    .addObject("chartFilesURL", chartFilesURL)
                    .addObject("chart", chartDto);
	}


	@RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.DELETE)
	public ModelAndView deleteChartItems(
                            @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                            @PathVariable("chartId") Integer chartId) {

		LOGGER.debug("input parameters selectedPublishDateTime=[{}], chartId=[{}]", selectedPublishDateTime, chartId);

		chartService.deleteChartItems(chartId, selectedPublishDateTime);
        return new ModelAndView("redirect:/charts/" + chartId);
	}

    private String getSelectedPublishDateAsString(Date selectedPublishDateTime, List<ChartItemDto> chartItemDtos) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(URL_DATE_TIME_FORMAT);
        if (chartItemDtos.isEmpty()) {
            return simpleDateFormat.format(selectedPublishDateTime);
        } else {
            return simpleDateFormat.format(chartItemDtos.get(0).getPublishTime());
        }
    }

    public void setViewByChartType(Map<ChartType, String> viewByChartType) {
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

    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public void setChartAsm(ChartAsm chartAsm) {
        this.chartAsm = chartAsm;
    }
}
