package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.validator.OfferDtoValidator;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.FilterService;
import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.shared.dto.ItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.enums.CurrencyCode;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
@Controller
public class OfferController extends AbstractCommonController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);

	private OfferService offerService;
	private FilterService filterService;
	private CloudFileService cloudFileService;

	public void setOfferService(OfferService offerService) {
		this.offerService = offerService;
	}

	public void setFilterService(FilterService filterService) {
		this.filterService = filterService;
	}
	
	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}

	@InitBinder(OfferDto.OFFER_DTO)
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new OfferDtoValidator());

		binder.registerCustomEditor(Set.class, "filterDtos", new CustomCollectionEditor(Set.class) {
			@Override
			protected Object convertElement(Object element) {
				FilterDto selectedfilterDto = null;
				if (element instanceof FilterDto) {
					selectedfilterDto = (FilterDto) element;
				} else if (element instanceof String) {
					Set<FilterDto> allOfferFilterDtos = getAllOfferFilterDtos();
					for (FilterDto filterDto : allOfferFilterDtos) {
						if (filterDto.getName().equals(element)) {
							selectedfilterDto = filterDto;
							break;
						}
					}
				}
				return selectedfilterDto;
			}
		});
		
		binder.registerCustomEditor(List.class, "itemDtos", new CustomCollectionEditor(List.class) {
			@Override
			protected Object convertElement(Object element) {
				
				ItemDto selectedItemDto = null;
				if (element instanceof ItemDto) {
					selectedItemDto = (ItemDto) element;
				} else if (element instanceof String) {
					try {
						Integer id = Integer.parseInt((String)element);
						selectedItemDto = offerService.getItemById(id);
					} catch (NumberFormatException e) {
						LOGGER.warn("String does not contain a parsable integer. Cause: {}", e);

					}
				}
				
				return selectedItemDto;
			}
		});

	}
	
	@ModelAttribute("filesURL")
	public String getFilesURL() {
		String fileURL = cloudFileService.getFilesURL();
		LOGGER.debug("Output parameter fileURL=[{}]", fileURL);
		return fileURL;
	}

	@ModelAttribute("allOfferFilterDtos")
	public Set<FilterDto> getAllOfferFilterDtos() {
		Set<FilterDto> allOfferFilterDtos = filterService.getAllFilters();
		LOGGER.debug("Output parameter allOfferFilterDtos=[{}]", allOfferFilterDtos);
		return allOfferFilterDtos;
	}
	
	@ModelAttribute("allCurrencyCodes")
	public CurrencyCode[] populateAllCurrencyCodes() {
		CurrencyCode[] allCurrencyCodes = CurrencyCode.values();
		LOGGER.debug("Output parameter [{}]",  allCurrencyCodes);
		return allCurrencyCodes;
	}

	@RequestMapping(value = "/offers", method = RequestMethod.GET)
	public ModelAndView getOffersPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", request);

		String communityURL = RequestUtils.getCommunityURL();

		List<OfferDto> offerDtos = offerService.getOffers(communityURL);

		ModelAndView modelAndView = new ModelAndView("offer/offers");
		modelAndView.getModelMap().put(OfferDto.OFFER_DTO_LIST, offerDtos);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = "/offers/allItems", method = RequestMethod.GET)
	@ResponseBody
	public List<ItemDto> getAllItems(HttpServletRequest request, @RequestParam("filter") String filter) {
		LOGGER.debug("input parameters request [{}]", request);

		List<ItemDto> itemDtos = offerService.getAllItems(filter);

		LOGGER.debug("Output parameter itemDtos=[{}]", itemDtos);
		return itemDtos;
	}

	@RequestMapping(value = "/offers/new", method = RequestMethod.GET)
	public ModelAndView getAddOfferPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", request);

		ModelAndView modelAndView = new ModelAndView("offer/add");
		modelAndView.getModelMap().put(OfferDto.OFFER_DTO, new OfferDto());

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/offers/new", method = RequestMethod.POST)
	public ModelAndView saveOffer(HttpServletRequest request, @Valid @ModelAttribute(OfferDto.OFFER_DTO) OfferDto offerDto,
			BindingResult bindingResult) {

		LOGGER.debug("input parameters request, offerDto, bindingResult: [{}], [{}], [{}]", new Object[] { request, offerDto, bindingResult });

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			modelAndView = new ModelAndView("offer/add");
			
		} else {

			String communityURL = RequestUtils.getCommunityURL();

			offerService.saveOffer(offerDto, communityURL);
			modelAndView = new ModelAndView("redirect:/offers");
		}
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = "/offers/{offerId}", method = RequestMethod.GET)
	public ModelAndView getUpdateOfferPage(HttpServletRequest request, @PathVariable("offerId") Integer offerId) {
		LOGGER.debug("input parameters request, offerId: [{}], [{}]", request, offerId);

		OfferDto offerDto = offerService.getOfferDto(offerId);

		ModelAndView modelAndView;
		if (offerDto != null) {
			modelAndView = new ModelAndView("offer/edit");
			modelAndView.getModelMap().put(OfferDto.OFFER_DTO, offerDto);
		} else
			modelAndView = new ModelAndView("redirect:/offers");

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/offers/{offerId}", method = RequestMethod.POST)
	public ModelAndView updateOffer(HttpServletRequest request, @Valid @ModelAttribute(OfferDto.OFFER_DTO) OfferDto offerDto,
			BindingResult bindingResult) {

		LOGGER.debug("input parameters request, offerDto, bindingResult: [{}], [{}], [{}]", new Object[] { request, offerDto, bindingResult });

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			modelAndView = new ModelAndView("offer/edit");
			modelAndView.getModelMap().put(OfferDto.OFFER_DTO, offerDto);
		} else {

			String communityURL = RequestUtils.getCommunityURL();

			Offer offer = offerService.updateOffer(offerDto, communityURL);
			if (offer == null) {
				modelAndView = new ModelAndView("offer/edit");
				modelAndView.getModelMap().put(OfferDto.OFFER_DTO, offerDto);
				bindingResult.addError(new ObjectError(OfferDto.OFFER_DTO, new String[] { "offer.edit.error.couldNotFindOffer" }, null,
						"Couldn't find this offer in the DB. To save it as new item click 'Save changes' button."));
			} else
				modelAndView = new ModelAndView("redirect:/offers");
		}
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/offers/{offerId}", method = RequestMethod.DELETE)
	public ModelAndView delete(HttpServletRequest request, @PathVariable("offerId") Integer offerId) {
		LOGGER.debug("input parameters request, offerId: [{}], [{}]", request, offerId);

		offerService.delete(offerId);

		ModelAndView modelAndView = new ModelAndView("redirect:/offers");

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
}