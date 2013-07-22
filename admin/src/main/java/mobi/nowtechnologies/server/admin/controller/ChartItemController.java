package mobi.nowtechnologies.server.admin.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mobi.nowtechnologies.server.assembler.ChartAsm;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.*;

import static mobi.nowtechnologies.server.shared.Utils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.ChartType.VIDEO_CHART;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.VIDEO;
import static org.springframework.util.StringUtils.hasText;

@Controller
public class ChartItemController extends AbstractCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);
    private static final String CHANNELS_CODE = "chartItems.channel.list";

    private ChartDetailService chartDetailService;
    private MediaService mediaService;
    private String filesURL;
    private String chartFilesURL;
    private ChartService chartService;
    private Map<String, String> viewByChartType;

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setViewByChartType(Map<String, String> viewByChartType) {
        this.viewByChartType = viewByChartType;
    }

    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setFilesURL(String filesURL) {
        this.filesURL = filesURL;
    }

    public void setChartFilesURL(String chartFilesURL) {
        this.chartFilesURL = chartFilesURL;
    }

    @ModelAttribute("chartFilesURL")
    public String getFilesURL() {
        return chartFilesURL;
    }

    /**
     * Getting chart item list for selected date
     *
     * @param selectedPublishDateTime - selected date and time represented in URI
     * @param chartId                 - chart identifier of selected chart
     * @return madel and view name of chart item list page
     */
    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = RequestMethod.GET)
    public ModelAndView getChartItemsPage(
            @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
            @PathVariable("chartId") Integer chartId,
            @RequestParam(value = "changePosition", required = false) boolean changePosition,
            Locale locale) {
        LOGGER.debug("input parameters request getChartItemsPage(selectedPublishDateTime, chartId): [{}], [{}]", new Object[]{selectedPublishDateTime, chartId});

        Chart chart = chartService.getChartById(chartId);
        ChartDetail chartDetail = chartService.getChartDetails(Collections.singletonList(chart), selectedPublishDateTime, true).get(0);
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(chartId, selectedPublishDateTime, changePosition);
        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(chartDetails);
        ChartDto chartDto = ChartAsm.toChartDto(chartDetail);

        Collection<String> allChannels = new HashSet<String>(getInitChannels(locale));
        allChannels.addAll(chartDetailService.getAllChannels());
        allChannels = new LinkedList<String>(allChannels);
        Collections.sort((List<String>) allChannels);

        return new ModelAndView(viewByChartType.get(chart.getType().name()))
                .addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos)
                .addObject("selectedPublishDateTime", selectedPublishDateTime)
                .addObject("filesURL", filesURL)
                .addObject("allChannels", allChannels)
                .addObject("chart", chartDto)
                .addObject("chartType", VIDEO_CHART.equals(chart.getType()) ? "video" : "media");
    }

    protected List<String> getInitChannels(Locale locale) {
        List<String> channelList = new ArrayList<String>();
        channelList.add("");

        String channels = messageSource.getMessage(CHANNELS_CODE, null, locale);
        if (hasText(channels)) {
            String[] items = channels.split(",");
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                if (!item.isEmpty())
                    channelList.add(item);
            }
        }

        return channelList;
    }

    /**
     * Updating or creating chart item list for selected date
     *
     * @param selectedPublishDateTime - selected date and time represented in URI
     * @param chartId                 - chart identifier of selected chart
     * @return redirect to current chart page
     */
    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
    public ModelAndView updateChartItems(@RequestBody String chartItemListJSON,
                                         @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Integer chartId) {

        LOGGER.debug("input parameters request updateChartItems(chartItemListJSON, selectedPublishDateTime, chartId): [{}], [{}], [{}]", new Object[]{chartItemListJSON, selectedPublishDateTime, chartId});

        Gson gson = new GsonBuilder().setDateFormat(URL_DATE_TIME_FORMAT).create();
        Type type = new TypeToken<ArrayList<ChartItemDto>>() {}.getType();

        List<ChartItemDto> chartItems = gson.fromJson(chartItemListJSON, type);

        chartDetailService.saveChartItems(chartItems);

        ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId);
        return modelAndView;
    }

    /**
     * Returning a page for creating a new chart item
     *
     * @param searchWords - search criteria of media by general propeties
     * @return only model of media list in chart item list structure
     */
    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}/{mediaType:media}/list", method = RequestMethod.GET)
    public ModelAndView getMediaList(
            @RequestParam(value = "q", required = false) String searchWords,
            @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
            @PathVariable("chartId") Integer chartId,
            @PathVariable("mediaType") String mediaType) {

        LOGGER.debug("input parameters request getMediaList(searchWords, selectedPublishDateTime, chartId): [{}], [{}], [{}]", searchWords, selectedPublishDateTime, chartId);

        List<Media> medias = getMedias(searchWords, mediaType);

        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtosFromMedia(selectedPublishDateTime, chartId, medias);

        return new ModelAndView()
                .addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
    }

    private List<Media> getMedias(String searchWords, String mediaType) {
        if(!hasText(searchWords)) return Collections.<Media>emptyList();
        if("media".equals(mediaType))
            return mediaService.getMusic(searchWords);
        else
            return mediaService.getVideo(searchWords);
    }

    /**
     * Update the chart items publishTime
     *
     * @param selectedPublishDateTime - selected date and time of chart items publishTime
     * @param newPublishDateTime      - new date and time of chart items publishTime
     * @param chartId                 - selected chart id
     * @return - returns the user to a list of the chart items for selected date
     */
    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}/{newPublishDateTime}", method = RequestMethod.POST)
    public ModelAndView updateChartItems(HttpServletResponse response,
                                         @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                         @PathVariable("newPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date newPublishDateTime,
                                         @PathVariable("chartId") Integer chartId) {

        LOGGER.debug("input parameters response, selectedPublishDateTime, newPublishDateTime, chartId: [{}], [{}]", new Object[]{response, selectedPublishDateTime, newPublishDateTime, chartId});

        ModelAndView modelAndView;
        try {
            chartDetailService.updateChartItems(chartId, selectedPublishDateTime.getTime(), newPublishDateTime.getTime());
            modelAndView = new ModelAndView("redirect:/chartsNEW/" + chartId + "/" + dateTimeFormat.format(newPublishDateTime));
        } catch (ServiceCheckedException e) {
            LOGGER.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            modelAndView = new ModelAndView("errors");
            modelAndView.addObject("errorCode", e.getErrorCodeForMessageLocalization());
        }

        LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }
}
