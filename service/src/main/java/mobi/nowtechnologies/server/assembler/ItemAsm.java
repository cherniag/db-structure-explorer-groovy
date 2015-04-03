package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Item;
import mobi.nowtechnologies.server.shared.dto.ItemDto;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class ItemAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemAsm.class);

    public static List<ItemDto> toItemDtos(List<Item> items) {
        LOGGER.debug("input parameters items: [{}]", items);

        List<ItemDto> itemDtos = new LinkedList<ItemDto>();

        if (items == null) {
            return itemDtos;
        }

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

}
