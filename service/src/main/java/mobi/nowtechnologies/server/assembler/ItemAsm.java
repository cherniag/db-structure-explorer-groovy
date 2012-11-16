package mobi.nowtechnologies.server.assembler;

import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Item;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.ItemDto;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferItemDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class ItemAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemAsm.class);

	public static List<ItemDto> toItemDtos(List<Item> items) {
		LOGGER.debug("input parameters items: [{}]", items);

		List<ItemDto> itemDtos = new LinkedList<ItemDto>();
		
		if(items == null)
			return itemDtos;

		for (Item Item : items) {
			itemDtos.add(toItemDto(Item));
		}

		LOGGER.debug("Output parameter [{}]", itemDtos);
		return itemDtos;
	}

	public static ItemDto toItemDto(Item item) {

		ItemDto itemDto = new ItemDto();

		itemDto.setId(item.getI());
		itemDto.setTitle(item.getTitle());
		itemDto.setPrice(item.getPrice());
		itemDto.setTypeId(item.getTypeId());

		LOGGER.debug("Output parameter ItemDto=[{}]", itemDto);
		return itemDto;
	}

	public static List<ContentOfferItemDto> toContentOfferItemDto(List<Item> items) {
		LOGGER.debug("input parameters items: [{}]", items);

		List<ContentOfferItemDto> contentOfferItemDtos = new LinkedList<ContentOfferItemDto>();
		for (Item item : items) {
			contentOfferItemDtos.add(toContentOfferItemDto(item));
		}

		LOGGER.debug("Output parameter [{}]", contentOfferItemDtos);
		return contentOfferItemDtos;
	}

	public static ContentOfferItemDto toContentOfferItemDto(Item item) {
		LOGGER.debug("input parameters item: [{}]", item);

		ContentOfferItemDto contentOfferItemDto = new ContentOfferItemDto();
		contentOfferItemDto.setTitle(item.getTitle());
		if (item instanceof Media) {
			final Media media = (Media) item;
			contentOfferItemDto.setCoverFileName(media.getImageFileSmall().getFilename());
			contentOfferItemDto.setAuthorName(media.getArtist().getName());
		} else
			throw new ServiceException("Unknown type [" + item.getClass() + "] of item [" + item + "]");

		LOGGER.debug("Output parameter [{}]", contentOfferItemDto);
		return contentOfferItemDto;
	}
}
