package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.asm.ChartAsm;
import mobi.nowtechnologies.server.admin.validator.ChartDtoValidator;
import mobi.nowtechnologies.server.admin.validator.ChartItemDtoValidator;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// @author Titov Mykhaylo (titov)
@Controller
public class ChartController extends AbstractCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

    private ChartService chartService;
    private ChartRepository chartRepository;
    private ChartDetailService chartDetailService;
    private ChartItemController chartItemController;
    private String filesURL;
    private String chartFilesURL;
    private Map<ChartType, String> viewByChartType;
    private ChartAsm chartAsm;
    private ChartDetailRepository chartDetailRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(URL_DATE_TIME_FORMAT), true));
    }

    @InitBinder({ChartItemDto.CHART_ITEM_DTO})
    public void initChartItemBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "channel", new StringTrimmerEditor(" ", true));
        binder.setValidator(new ChartItemDtoValidator());
    }

    @InitBinder({ChartDto.CHART_DTO})
    public void initChartBinder(WebDataBinder binder) {
        ChartDtoValidator chartDtoValidator = new ChartDtoValidator();
        chartDtoValidator.setChartService(chartService);
        binder.setValidator(chartDtoValidator);
    }

    @RequestMapping(value = "/charts/list", method = RequestMethod.GET)
    public ModelAndView getCharts(HttpServletRequest request) {
        LOGGER.debug("input parameters request [{}]", new Object[] {request});

        String communityURL = RequestUtils.getCommunityURL();
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, null);
        List<ChartDto> chartDTOs = chartAsm.toChartDtos(charts);

        return new ModelAndView("charts/charts").addObject(ChartDto.CHART_DTO_LIST, chartDTOs);
    }

    @RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
    public ModelAndView updateChart(@PathVariable("chartId") Integer chartId, @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                    @Valid @ModelAttribute(ChartDto.CHART_DTO) ChartDto chartDto,
                                    @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl, BindingResult bindingResult, Locale locale) {

        LOGGER.debug("input parameters chartDto, chartId: [{}], [{}], [{}]", new Object[] {chartDto, chartId});

        ModelAndView modelAndView;

        if (!bindingResult.hasErrors()) {
            ChartDetail chartDetail = chartAsm.toChart(chartDto);
            chartDetail.setPublishTimeMillis(selectedPublishDateTime.getTime());
            chartService.updateChart(chartDetail, chartDto.getFile());
            modelAndView = new ModelAndView("redirect:/charts/" + chartId);
        } else {
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

        Chart chart = chartRepository.findOne(chartId);
        ChartDetail chartDetail = chartService.getChartDetails(Collections.singletonList(chart), selectedPublishDateTime, false).get(0);
        List<ChartDetail> chartItems = chartDetailService.getActualChartItems(chartId, selectedPublishDateTime);
        List<ChartItemDto> chartItemDTOs = ChartDetailsAsm.toChartItemDtos(chartItems);
        ChartDto chartDto = chartAsm.toChartDto(chartDetail);

        return new ModelAndView(viewByChartType.get(chart.getType())).addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDTOs)
                                                                     .addObject("selectedPublishDateTime", getSelectedPublishDateAsString(selectedPublishDateTime, chartItemDTOs))
                                                                     .addObject("selectedDateTime", selectedPublishDateTime)
                                                                     .addObject("allPublishTimeMillis", chartDetailRepository.findAllPublishTimeMillis(chartId)).addObject("filesURL", filesURL)
                                                                     .addObject("chartFilesURL", chartFilesURL).addObject("chart", chartDto);
    }


    @RequestMapping(value = "/charts/{chartId}/{selectedPublishDateTime}", method = RequestMethod.DELETE)
    public ModelAndView deleteChartItems(@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                         @PathVariable("chartId") Integer chartId) {

        LOGGER.debug("input parameters selectedPublishDateTime=[{}], chartId=[{}]", selectedPublishDateTime, chartId);

        chartDetailService.deleteChartItems(chartId, selectedPublishDateTime.getTime());
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

    public void setChartAsm(ChartAsm chartAsm) {
        this.chartAsm = chartAsm;
    }

    public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
        this.chartDetailRepository = chartDetailRepository;
    }

    public void setChartRepository(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }
}
