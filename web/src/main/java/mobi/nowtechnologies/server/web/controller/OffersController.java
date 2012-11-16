package mobi.nowtechnologies.server.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
public class OffersController extends CommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(OffersController.class);

	private OfferService offerService;
	private String filesURL;
	private String coverStorePath;

	public void setOfferService(OfferService offerService) {
		this.offerService = offerService;
	}
	
	public void setFilesURL(String filesURL) {
		this.filesURL = filesURL;
	}
	
	public void setCoverStorePath(String coverStorePath) {
		this.coverStorePath = coverStorePath;
	}
	
	@ModelAttribute("filesURL")
	public String getFilesURL() {
		return filesURL;
	}
	
	@ModelAttribute("coverStorePath")
	public String getCoverStorePath() {
		return coverStorePath;
	}
	
//	@ModelAttribute("imageSmallFileType")
//	public FileType getImageSmallFileType() {
//		return FileType.IMAGE_SMALL;
//	}
	
	@RequestMapping(value = "/offers.html", method = RequestMethod.GET)
	public ModelAndView getOffersPage(HttpServletRequest httpServletRequest) {
		LOGGER.debug("input parameters httpServletRequest: [{}] ", httpServletRequest);

		int userId = getUserId();

		List<ContentOfferDto> contentOfferDtos = offerService.getContentOfferDtos(userId);
		if (contentOfferDtos.isEmpty()) {
			return new ModelAndView("redirect:/payments.html");
		}
		
		ModelAndView modelAndView = new ModelAndView("offers");
		modelAndView.addObject(ContentOfferDto.OFFER_DTO_LIST, contentOfferDtos);

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = "/offers/{offerId}", method = RequestMethod.GET)
	public ModelAndView getOfferPage(HttpServletRequest httpServletRequest, @PathVariable("offerId") Integer offerId) {
		LOGGER.debug("input parameters httpServletRequest, offerId: [{}], [{}]", httpServletRequest, offerId);

		ContentOfferDto contentOfferDto = offerService.getContentOfferDto(offerId);

		ModelAndView modelAndView = new ModelAndView("offer");
		modelAndView.addObject(ContentOfferDto.OFFER_DTO, contentOfferDto);

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

}
