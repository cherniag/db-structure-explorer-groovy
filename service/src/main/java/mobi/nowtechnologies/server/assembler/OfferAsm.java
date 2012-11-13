package mobi.nowtechnologies.server.assembler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.ItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class OfferAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferAsm.class);
	
	public static List<OfferDto> toOfferDtos(List<Offer> offers) {
		LOGGER.debug("input parameters offers: [{}]", offers);
		
		List<OfferDto> offerDtos = new LinkedList<OfferDto>();
		
		for (Offer offer : offers) {
			offerDtos.add(toOfferDto(offer));
		}
		
		LOGGER.debug("Output parameter [{}]", offerDtos);
		return offerDtos;
	}

	public static OfferDto toOfferDto(Offer offer) {
		LOGGER.debug("input parameters offer: [{}]", offer);
		
		OfferDto offerDto = new OfferDto(); 
		
		offerDto.setId(offer.getId());
		offerDto.setTitle(offer.getTitle());
		offerDto.setPrice(offer.getPrice());
		offerDto.setCurrency(offer.getCurrency());
		offerDto.setCoverFileName(offer.getCoverFileName());
		offerDto.setDescription(offer.getDescription());
		
		Set<FilterDto> filterDtos = FilterAsm.toDtos(offer.getFilterWithCtiteria());
		offerDto.setFilterDtos(filterDtos);
		
		List<ItemDto> itemDtos = ItemAsm.toItemDtos(offer.getItems());
		offerDto.setItemDtos(itemDtos);
		
		LOGGER.debug("Output parameter offerDto=[{}]", offerDto);
		return offerDto;
	}

	public static List<ContentOfferDto> toContentOfferDtos(List<Offer> offers, User user, List<Integer> boughtOfferIds) {
		LOGGER.debug("input parameters offers, user: [{}], [{}]", offers, user);
		
		List<ContentOfferDto> contentOfferDtos = new LinkedList<ContentOfferDto>();
		for (Offer offer : offers) {
			final Set<AbstractFilterWithCtiteria> filterWithCtiteriaSet = offer.getFilterWithCtiteria();
			boolean filtrate = true;
			for (AbstractFilterWithCtiteria abstractFilterWithCtiteria : filterWithCtiteriaSet) {
				filtrate = abstractFilterWithCtiteria.doFilter(user);
				if (filtrate == false)
					break;
			}
			if (filtrate && !boughtOfferIds.contains(offer.getId()))
				contentOfferDtos.add(toContentOfferDto(offer));
		}		
		
		LOGGER.debug("Output parameter [{}]", contentOfferDtos);
		return contentOfferDtos;
	}

	public static ContentOfferDto toContentOfferDto(Offer offer) {
		LOGGER.debug("input parameters offer: [{}]", offer);
		
		ContentOfferDto contentOfferDto = new ContentOfferDto();
		contentOfferDto.setCurrency(offer.getCurrency());
		contentOfferDto.setId(offer.getId());
		contentOfferDto.setPrice(offer.getPrice());
		contentOfferDto.setTitle(offer.getTitle());
		contentOfferDto.setCoverFileName(offer.getCoverFileName());
		contentOfferDto.setDescription(offer.getDescription());
		
		contentOfferDto.setContentOfferItemDtos(ItemAsm.toContentOfferItemDto(offer.getItems()));
		
		LOGGER.debug("Output parameter [{}]", contentOfferDto);
		return contentOfferDto;
	}
}
