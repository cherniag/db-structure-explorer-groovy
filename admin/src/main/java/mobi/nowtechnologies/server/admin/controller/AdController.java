package mobi.nowtechnologies.server.admin.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import mobi.nowtechnologies.server.admin.validator.AdItemDtoValidator;
import mobi.nowtechnologies.server.dto.AdItemDto;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.FilterService;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class AdController extends AbstractCommonController {
	
	private static final String ERRORS = "errors";

	private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);
	
	private MessageService messageService;
	private FilterService filterService;
	private CloudFileService cloudFileService;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setFilterService(FilterService filterService) {
		this.filterService = filterService;
	}
	
	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}
	
	@InitBinder( { AdItemDto.NAME })
	public void initAdBinder(WebDataBinder binder) {
		binder.setValidator(new AdItemDtoValidator());
		
		binder.registerCustomEditor(Set.class, "filterDtos", new CustomCollectionEditor(Set.class) {
			protected Object convertElement(Object element) {
				FilterDto selectedfilterDto = null;
				if (element instanceof FilterDto) {
					selectedfilterDto = (FilterDto) element;
				} else if (element instanceof String) {
					Set<FilterDto> allMessageFilterDtos = populateAllMessageFilterDtos();
					for (FilterDto filterDto : allMessageFilterDtos) {
						if (filterDto.getName().equals(element)) {
							selectedfilterDto = filterDto;
							break;
						}
					}
				}
				return selectedfilterDto;
			}
		});
	}
	
	
	@ModelAttribute("filesURL")
	public String getFilesURL() {
		return cloudFileService.getFilesURL();
	}
	
	@ModelAttribute("allAdFilterDtos")
	public Set<FilterDto> populateAllMessageFilterDtos() {
		return filterService.getAllFilters();
	}
	
	@RequestMapping(value = "/ads", method = RequestMethod.GET)
	public ModelAndView getAdsPage(HttpServletRequest request) {
		String communityURL = RequestUtils.getCommunityURL();

		List<Message> messages = messageService.getAds(communityURL);
		List<AdItemDto> adItemDtos = AdItemDto.toDtoList(messages);

		ModelAndView modelAndView = new ModelAndView("ads/ads");
		modelAndView.getModelMap().put(AdItemDto.LIST, adItemDtos);

		return modelAndView;
	}

	@RequestMapping(value = "/ads/", method = RequestMethod.GET)
	public ModelAndView getAddAdPage(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView("ads/add");
		modelAndView.getModelMap().put(AdItemDto.NAME, new AdItemDto());

		return modelAndView;
	}
	
	@RequestMapping(value = "/ads/", method = RequestMethod.POST)
	public ModelAndView saveAd(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute(AdItemDto.NAME) AdItemDto adItemDto,
			BindingResult bindingResult) {

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
			modelAndView = new ModelAndView("ads/add");
			modelAndView.addObject(ERRORS, bindingResult.getAllErrors());
		} else {

			String communityURL = RequestUtils.getCommunityURL();

			Message message = messageService.saveAd(AdItemDto.fromDto(adItemDto), adItemDto.getFile(), communityURL, adItemDto.getFilterDtos(), adItemDto.isRemoveImage());
			LOGGER.info("The advertisement has been saved as [" + message + "] successfully");
			modelAndView = new ModelAndView("redirect:/ads");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/ads/{adItemId}", method = RequestMethod.POST)
	public ModelAndView updateAd(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute(AdItemDto.NAME) AdItemDto adItemDto,
			BindingResult bindingResult) {

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
			modelAndView = new ModelAndView("ads/edit");
			if (adItemDto.getFilterDtos() == null) {
				adItemDto.setFilterDtos(Collections.<FilterDto> emptySet());
			}
			modelAndView.getModelMap().put(AdItemDto.NAME, adItemDto);
			modelAndView.addObject(ERRORS, bindingResult.getAllErrors());
		} else {
			
			String communityURL = RequestUtils.getCommunityURL();

			Message message = messageService.updateAd(AdItemDto.fromDto(adItemDto), adItemDto.getFile(), communityURL, adItemDto.getFilterDtos(), adItemDto.isRemoveImage());
			if (message == null) {
				modelAndView = new ModelAndView("ads/add");
				adItemDto.setId(null);
				modelAndView.getModelMap().put(AdItemDto.NAME, adItemDto);
				response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
				bindingResult.addError(new ObjectError(AdItemDto.NAME, new String[] { "message.edit.error.couldNotFindMessage" }, null,
						"Couldn't find this message in the DB. To save it as new item click 'Save changes' button."));
				modelAndView.addObject(ERRORS, bindingResult.getAllErrors());
			} else{
				LOGGER.info("The advertisement has been updated on [" + message + "] successfully");
				modelAndView = new ModelAndView("redirect:/ads");
			}
		}
		return modelAndView;
	}

	@RequestMapping(value = "/ads/{adItemId}", method = RequestMethod.GET)
	public ModelAndView getUpdateAdPage(HttpServletRequest request, @PathVariable("adItemId") Integer adItemId) {

		Message message = messageService.getMessageWithFilters(adItemId);

		ModelAndView modelAndView;
		if (message != null) {
			AdItemDto adItemDto = AdItemDto.toDtoItem(message);
			
			modelAndView = new ModelAndView("ads/edit");
			modelAndView.getModelMap().put(AdItemDto.NAME, adItemDto);
		} else
			modelAndView = new ModelAndView("redirect:/ads");

		return modelAndView;
	}

	@RequestMapping(value = "/ads/{adItemId}", method = RequestMethod.DELETE)
	public ModelAndView delete(HttpServletRequest request, @PathVariable("adItemId") Integer adItemId) {
	
		messageService.delete(adItemId);
		
		LOGGER.info("The advertisement with id [" + adItemId + "] has been removed successfully");

		ModelAndView modelAndView = new ModelAndView("redirect:/ads");

		return modelAndView;
	}
}
