package mobi.nowtechnologies.server.admin.controller;

import java.lang.reflect.Type;
import java.util.*;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Controller
public class ChartItemController extends AbstractCommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);
	private static final String CHANNELS_CODE = "chartItems.channel.list";
	
	private ChartDetailService chartDetailService;
	private MediaService mediaService;
	private String filesURL;

	public void setChartDetailService(ChartDetailService chartDetailService) {
		this.chartDetailService = chartDetailService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public void setFilesURL(String filesURL) {
		this.filesURL = filesURL;
	}

	/**
	 * Getting chart item list for selected date
	 * 
	 * @param selectedPublishDateTime - selected date and time represented in URI
	 * @param chartId - chart identifier of selected chart
	 * 
	 * @return madel and view name of chart item list page
	 */
	@RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = RequestMethod.GET)
	public ModelAndView getChartItemsPage(@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId, @RequestParam(value="changePosition", required=false) boolean changePosition, Locale locale) {

		LOGGER.debug("input parameters request getChartItemsPage(selectedPublishDateTime, chartId): [{}], [{}]", new Object[] { selectedPublishDateTime, chartId });

		List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(chartId, selectedPublishDateTime, changePosition);
		List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtos(chartDetails);
		
		Collection<String> allChannels = new HashSet<String>(getInitChannels(locale));
		allChannels.addAll(chartDetailService.getAllChannels());
		allChannels = new LinkedList<String>(allChannels);
		Collections.sort((List<String>)allChannels);

		ModelAndView modelAndView = new ModelAndView("chartItems/chartItemsNEW");
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		modelAndView.addObject("selectedPublishDateTime", selectedPublishDateTime);
		modelAndView.addObject("filesURL", filesURL);
		modelAndView.addObject("allChannels", allChannels);
		modelAndView.addObject("chartId", chartId);

		return modelAndView;
	}
	
	protected List<String> getInitChannels(Locale locale){
		List<String> channelList = new ArrayList<String>(); 
		channelList.add("");
		
		String channels = messageSource.getMessage(CHANNELS_CODE, null, locale);
		if(StringUtils.hasText(channels)){
			String[] items = channels.split(",");
			for (int i = 0; i < items.length; i++) {
				String item = items[i].trim();
				if(!item.isEmpty())
					channelList.add(item);
			}
		}
		
		return channelList;
	}
	
	/**
	 * Updating or creating chart item list for selected date
	 *
	 * @param chartItemsListJSON - chart item list in JSON data format
	 * @param selectedPublishDateTime - selected date and time represented in URI
	 * @param chartId - chart identifier of selected chart
	 * 
	 * @return redirect to current chart page
	 */
	@RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}", method = RequestMethod.POST)
	public ModelAndView updateChartItems(@RequestBody String chartItemListJSON,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {

		LOGGER.debug("input parameters request updateChartItems(chartItemListJSON, selectedPublishDateTime, chartId): [{}], [{}], [{}]", new Object[] {chartItemListJSON, selectedPublishDateTime, chartId });

		Gson gson = new GsonBuilder().setDateFormat(URL_DATE_TIME_FORMAT).create();
		Type type = new TypeToken<ArrayList<ChartItemDto>>(){}.getType();

		List<ChartItemDto> chartItems = gson.fromJson(chartItemListJSON, type);
		
		chartDetailService.saveChartItems(chartItems);
		
		ModelAndView modelAndView = new ModelAndView("redirect:/charts/" + chartId);
		return modelAndView;
	}
	
	/**
	 * Returning a page for creating a new chart item
	 * 
	 * @param searchWords - search criteria of media by general propeties
	 * 
	 * @return only model of media list in chart item list structure
	 */
	@RequestMapping(value = "/chartsNEW/{chartId}/{selectedPublishDateTime}/media/list", method = RequestMethod.GET)
	public ModelAndView getMediaList(@RequestParam(value = "q", required = false) String searchWords,
			@PathVariable("selectedPublishDateTime") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date selectedPublishDateTime, @PathVariable("chartId") Byte chartId) {
		
		LOGGER.debug("input parameters request getMediaList(searchWords, selectedPublishDateTime, chartId): [{}], [{}], [{}]", new Object[] {searchWords, selectedPublishDateTime, chartId });
		
		final List<Media> medias;
		if (searchWords == null) {
			medias = Collections.<Media>emptyList();
		} else
			medias = mediaService.getMedias(searchWords);

		List<ChartItemDto> chartItemDtos = ChartDetailsAsm.toChartItemDtosFromMedia(selectedPublishDateTime, chartId, medias);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		return modelAndView;
	}
}