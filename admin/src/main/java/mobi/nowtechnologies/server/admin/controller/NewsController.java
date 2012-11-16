package mobi.nowtechnologies.server.admin.controller;


import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import mobi.nowtechnologies.server.admin.validator.NewsItemDtoValidator;
import mobi.nowtechnologies.server.assembler.NewsAsm;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.shared.dto.admin.NewsItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.NewsPositionsDto;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class NewsController extends AbstractMessageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);

	private CloudFileService cloudFileService;

	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}

	@InitBinder({ NewsItemDto.NEWS_ITEM_DTO })
	public void initNewsBinder(WebDataBinder binder) {
		binder.setValidator(new NewsItemDtoValidator());
	}

	@ModelAttribute("filesURL")
	public String getFilesURL() {
		return cloudFileService.getFilesURL();
	}

	/**
	 * Getting calendar look for picking up a date and list of news for current date
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/news", method = RequestMethod.GET)
	public ModelAndView getNewsCalendarPage(HttpServletRequest request,
			@RequestParam(required = false, value = "selectedPublishDate", defaultValue = "") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
		String communityURL = RequestUtils.getCommunityURL();

		if (selectedPublishDate == null)
			selectedPublishDate = new Date();

		List<NewsItemDto> newsItemDtos = NewsAsm.toDtos(messageService.getActualNews(communityURL, selectedPublishDate));

		final String selectedPublishDateString;
		if (newsItemDtos.isEmpty()) {
			selectedPublishDateString = dateFormat.format(selectedPublishDate);
		} else {
			selectedPublishDateString = dateFormat.format(newsItemDtos.get(0).getPublishTime());
		}

		ModelAndView modelAndView = new ModelAndView("news/newsCalendar");
		modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO_LIST, newsItemDtos);
		modelAndView.addObject("selectedPublishDate", selectedPublishDateString);
		modelAndView.addObject("allPublishTimeMillis", messageService.getAllPublishTimeMillis(communityURL));

		return modelAndView;
	}

	/**
	 * Getting news for selected date
	 * 
	 * @param request
	 * @param selectedPublishDate
	 *            - selected date represented in URI
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}", method = RequestMethod.GET)
	public ModelAndView getNewsPositionPage(HttpServletRequest request, @PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
		String communityURL = RequestUtils.getCommunityURL();
		List<NewsItemDto> newsItemDtos = messageService.getNewsByDate(communityURL, selectedPublishDate);

		ModelAndView modelAndView = new ModelAndView("news/news");
		modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO_LIST, newsItemDtos);
		modelAndView.addObject("selectedPublishDate", selectedPublishDate);

		return modelAndView;
	}

	/**
	 * Updating news position for selected date
	 * 
	 * @param request
	 * @param newsPositionsDto
	 *            - new positions of news
	 * @param selectedPublishDate
	 *            - selected date for which we update news position
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}", method = RequestMethod.POST)
	public ModelAndView updateNewsPositions(HttpServletRequest request, @ModelAttribute(NewsPositionsDto.NEWS_POSITIONS_DTO) NewsPositionsDto newsPositionsDto,
			@PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
		messageService.updateNewsPositions(newsPositionsDto);
		ModelAndView modelAndView = new ModelAndView("redirect:/news/" + dateFormat.format(selectedPublishDate));
		return modelAndView;
	}

	/**
	 * 
	 * @param request
	 * @param selectedPublishDate
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/list", method = RequestMethod.POST)
	public ModelAndView cloneNewsForSelectedPublishDateIfOnesDoesNotExsistForSelectedPublishDate(HttpServletRequest request, @PathVariable("selectedPublishDate") String selectedPublishDate) {
		LOGGER.debug("input parameters request, selectedPublishDate: [{}], [{}]", request, selectedPublishDate);

		String communityURL = RequestUtils.getCommunityURL();
		final Date choosedPublishDate = validateSelectedPublishDate(selectedPublishDate);

		messageService.cloneNewsForSelectedPublishDateIfOnesDoesNotExsistForSelectedPublishDate(choosedPublishDate, communityURL);
		ModelAndView modelAndView = new ModelAndView("redirect:/news/" + selectedPublishDate);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	/**
	 * Returning a page for creating a new news
	 * 
	 * @param request
	 * @param selectedPublishDate
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/new", method = RequestMethod.GET)
	public ModelAndView getNewNewsPage(HttpServletRequest request, @PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
		ModelAndView modelAndView = new ModelAndView("news/add");
		final NewsItemDto newsItemDto = new NewsItemDto();
		newsItemDto.setPublishTime(selectedPublishDate);
		modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO, newsItemDto);
		return modelAndView;
	}

	/**
	 * Storing newly created news
	 * 
	 * @param request
	 * @param newsItemDto
	 * @param bindingResult
	 * @param selectedPublishDate
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/new", method = RequestMethod.POST)
	public ModelAndView createNewNews(HttpServletRequest request, @Valid @ModelAttribute(NewsItemDto.NEWS_ITEM_DTO) NewsItemDto newsItemDto, BindingResult bindingResult,
			@PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) throws ParseException {
		LOGGER.debug("input parameters request, newsItemDto, bindingResult, selectedPublishDate: [{}], [{}], [{}], [{}]", new Object[] { request, newsItemDto, bindingResult,
				selectedPublishDate });

		final ModelAndView modelAndView;
		if (!bindingResult.hasErrors()) {
			newsItemDto.setPublishTime(selectedPublishDate);
			messageService.saveNews(newsItemDto, RequestUtils.getCommunityURL());
			modelAndView = new ModelAndView("redirect:/news/" + dateFormat.format(selectedPublishDate));
		} else {
			newsItemDto.setPublishTime(selectedPublishDate);
			modelAndView = new ModelAndView("news/add");
			modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO, newsItemDto);
		}
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	/**
	 * Getting a page for editing specific news for selected date
	 * 
	 * @param request
	 * @param messageId
	 *            - news id that needs to be edited
	 * @param selectedPublishDate
	 *            - selected date
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/{messageId}", method = RequestMethod.GET)
	public ModelAndView getNewsPage(HttpServletRequest request, @PathVariable("messageId") Integer messageId,
			@PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {

		NewsItemDto newsItemDto = messageService.getNewsById(messageId);

		if (newsItemDto != null) {
			ModelAndView modelAndView = new ModelAndView("news/edit");
			newsItemDto.setPublishTime(selectedPublishDate);
			modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO, newsItemDto);
			return modelAndView;
		}
		throw new ResourceNotFoundException("Can't find news for this id " + messageId);
	}

	/**
	 * Update particular news item according to selected date and news id
	 * 
	 * @param request
	 * @param newsItemDto
	 *            - news to be updated
	 * @param bindingResult
	 *            - result of binding firm inputs to java objects and validation results
	 * @param selectedPublishDate
	 *            - selected date
	 * @param messageId
	 *            - news id
	 * @return
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/{messageId}", method = RequestMethod.POST)
	public ModelAndView updateNews(HttpServletRequest request, @Valid @ModelAttribute(NewsItemDto.NEWS_ITEM_DTO) NewsItemDto newsItemDto, BindingResult bindingResult,
			@PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate, @PathVariable("messageId") Integer messageId) {

		ModelAndView modelAndView = new ModelAndView("news/edit");
		if (!bindingResult.hasErrors()) {
			String communityURL = RequestUtils.getCommunityURL();
			messageService.updateNews(newsItemDto, communityURL);
			modelAndView = new ModelAndView("redirect:/news/" + dateFormat.format(selectedPublishDate));
		} else {
			NewsItemDto oldNewsItemDto = messageService.getNewsById(messageId);
			newsItemDto.setImageFileName(oldNewsItemDto.getImageFileName());
			newsItemDto.setPublishTime(selectedPublishDate);
			modelAndView = new ModelAndView("news/edit");
			modelAndView.addObject(NewsItemDto.NEWS_ITEM_DTO, newsItemDto);
		}
		return modelAndView;
	}

	/**
	 * Deleting news by id according to selected date
	 * 
	 * @param request
	 * @param messageId
	 *            - id of the news
	 * @param selectedPublishDate
	 *            - selected date
	 * @return - returns the user to a list of the news for selected date
	 */
	@RequestMapping(value = "/news/{selectedPublishDate}/{messageId}", method = RequestMethod.DELETE)
	public ModelAndView deleteNews(HttpServletRequest request, @PathVariable("messageId") Integer messageId,
			@PathVariable("selectedPublishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
		messageService.delete(messageId);
		ModelAndView modelAndView = new ModelAndView("redirect:/news/" + dateFormat.format(selectedPublishDate));
		return modelAndView;
	}

}
