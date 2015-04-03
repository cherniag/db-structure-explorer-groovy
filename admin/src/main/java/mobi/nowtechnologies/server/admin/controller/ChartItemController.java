package mobi.nowtechnologies.server.admin.controller;


import mobi.nowtechnologies.server.admin.asm.ChartAsm;
import mobi.nowtechnologies.server.admin.asm.DuplicatedMediaAcrossNearestChartsDtoAssembler;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import static mobi.nowtechnologies.server.shared.enums.ChartType.VIDEO_CHART;
import static mobi.nowtechnologies.server.shared.web.utils.RequestUtils.getCommunityURL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ChartItemController extends AbstractCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);
    private static final String CHANNELS_CODE = "chartItems.channel.list";

    private TrackRepoService trackRepoService;
    private ChartDetailService chartDetailService;
    private MediaService mediaService;
    private String filesURL;
    private String chartFilesURL;
    private ChartService chartService;
    private Map<ChartType, String> viewByChartType;
    private ChartAsm chartAsm;
    private ChartRepository chartRepository;
    @Resource
    private DuplicatedMediaAcrossNearestChartsDtoAssembler duplicatedMediaAcrossNearestChartsDtoAssembler;

    @ModelAttribute("chartFilesURL")
    public String getFilesURL() {
        return chartFilesURL;
    }

    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = GET)
    public ModelAndView getChartItemsPage(@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                          @PathVariable("chartId") Integer chartId, @RequestParam(value = "changePosition", required = false) boolean changePosition, Locale locale) {
        LOGGER.info("input parameters chartId=[{}], selectedPublishDateTime=[{}], changePosition=[{}]", chartId, selectedPublishDateTime, changePosition);

        Chart chart = chartRepository.findOne(chartId);
        ChartDetail chartDetail = chartService.getChartDetails(Collections.singletonList(chart), selectedPublishDateTime, true).get(0);
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(chartId, selectedPublishDateTime, changePosition);
        List<ChartItemDto> chartItemDTOs = ChartDetailsAsm.toChartItemDtos(chartDetails);
        ChartDto chartDto = chartAsm.toChartDto(chartDetail);

        Collection<String> allChannels = new HashSet<String>(getInitChannels(locale));
        allChannels.addAll(chartDetailService.getAllChannels());
        allChannels = new LinkedList<String>(allChannels);
        Collections.sort((List<String>) allChannels);

        return new ModelAndView(viewByChartType.get(chart.getType())).addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDTOs).addObject("selectedPublishDateTime", selectedPublishDateTime)
                                                                     .addObject("filesURL", filesURL).addObject("chartFilesURL", chartFilesURL).addObject("allChannels", allChannels)
                                                                     .addObject("chart", chartDto).addObject("chartType", VIDEO_CHART.equals(chart.getType()) ?
                                                                                                                          "video" :
                                                                                                                          "media");
    }

    protected List<String> getInitChannels(Locale locale) {
        List<String> channelList = new ArrayList<String>();
        channelList.add("");

        String channels = messageSource.getMessage(CHANNELS_CODE, null, locale);
        if (hasText(channels)) {
            String[] items = channels.split(",");
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                if (!item.isEmpty()) {
                    channelList.add(item);
                }
            }
        }

        return channelList;
    }

    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = POST)
    public ModelAndView updateChartItems(@RequestBody List<ChartItemDto> chartItems,
                                         @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                         @PathVariable("chartId") Integer chartId) {

        LOGGER.debug("input parameters request updateChartItems(chartItemListJSON, selectedPublishDateTime, chartId): [{}], [{}], [{}]", chartItems, selectedPublishDateTime, chartId);

        chartDetailService.saveChartItems(chartItems);

        return new ModelAndView("redirect:/charts/" + chartId);
    }

    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}/{mediaType}/list", method = GET)
    public ModelAndView getMediaList(@RequestParam(value = "q", required = false) String searchWords,
                                     @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Integer chartId,
                                     @PathVariable("mediaType") String mediaType) {

        LOGGER.debug("input parameters request searchWords, selectedPublishDateTime, chartId, mediaType : [{}], [{}], [{}], [{}]", searchWords, selectedPublishDateTime, chartId, mediaType);

        List<Media> medias = getMedias(searchWords, mediaType);
        List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtosFromMedia(selectedPublishDateTime, chartId, medias);

        PageRequest pageRequest = new PageRequest(0, 10000);
        Map<Integer, String> terCodes = new HashMap<Integer, String>();

        for (Media m : medias) {
            SearchTrackDto searchTrackDto = new SearchTrackDto();
            List<Integer> trackId = new ArrayList<Integer>();
            if (m.getTrackId() != null) {
                trackId.add(m.getTrackId().intValue());
                searchTrackDto.setTrackIds(trackId);
                PageListDto<TrackDto> trackDtoPageListDto = trackRepoService.find(searchTrackDto, pageRequest);
                if ((trackDtoPageListDto != null) && (trackDtoPageListDto.getList().size() != 0)) {
                    terCodes.put(m.getI(), trackDtoPageListDto.getList().get(0).getTerritoryCodes());

                    for (ChartItemDto chartItemDto : chartItemDtos) {
                        if (chartItemDto.getMediaDto().getId().intValue() == m.getI().intValue()) {
                            chartItemDto.setCode(trackDtoPageListDto.getList().get(0).getTerritoryCodes());
                        }
                    }
                }
            }
        }

        terCodes.size();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
        modelAndView.addObject("territories", terCodes);

        return modelAndView;
    }

    @RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}/{newPublishDateTime}", method = POST)
    public ModelAndView updateChartItems(HttpServletResponse response, @PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime,
                                         @PathVariable("newPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date newPublishDateTime, @PathVariable("chartId") Integer chartId) {

        LOGGER.debug("input parameters response, selectedPublishDateTime, newPublishDateTime, chartId: [{}], [{}], [{}], [{}]", response, selectedPublishDateTime, newPublishDateTime, chartId);

        ModelAndView modelAndView;
        try {
            chartDetailService.updateChartItems(chartId, selectedPublishDateTime.getTime(), newPublishDateTime.getTime());
            modelAndView = new ModelAndView("redirect:/chartsNEW/" + chartId + "/" + new SimpleDateFormat(URL_DATE_TIME_FORMAT).format(newPublishDateTime));
        } catch (ServiceCheckedException e) {
            LOGGER.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            modelAndView = new ModelAndView("errors");
            modelAndView.addObject("errorCode", e.getErrorCodeForMessageLocalization());
        }

        LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/chartsNEW/{excludedChartId}/{selectedDateTime}/", params = "mediaId", method = GET)
    public ModelAndView getDuplicatedTracksForUpdatesAcrossCommunityPlayLists(@PathVariable @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedDateTime, @PathVariable int excludedChartId,
                                                                              @RequestParam("mediaId") List<Integer> mediaIds) {

        List<ChartDetail> duplicatedMediaChartDetails = chartService.getDuplicatedMediaChartDetails(getCommunityURL(), excludedChartId, selectedDateTime.getTime(), mediaIds);

        return new ModelAndView("", "duplicatedMediaAcrossNearestChartsDtos", duplicatedMediaAcrossNearestChartsDtoAssembler.getDuplicatedMediaAcrossNearestChartsDtos(duplicatedMediaChartDetails));
    }

    public List<Media> getMedias(String searchWords, String mediaType) {
        if (!hasText(searchWords)) {
            return Collections.<Media>emptyList();
        }
        if ("media".equals(mediaType)) {
            return mediaService.getMusic(searchWords);
        } else {
            return mediaService.getVideo(searchWords);
        }
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setViewByChartType(Map<ChartType, String> viewByChartType) {
        this.viewByChartType = viewByChartType;
    }

    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setChartFilesURL(String chartFilesURL) {
        this.chartFilesURL = chartFilesURL;
    }

    public void setTrackRepoService(TrackRepoService trackRepoService) {
        this.trackRepoService = trackRepoService;
    }

    public void setChartAsm(ChartAsm chartAsm) {
        this.chartAsm = chartAsm;
    }

    public void setChartRepository(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public void setFilesURL(String filesURL) {
        this.filesURL = filesURL;
    }
}
