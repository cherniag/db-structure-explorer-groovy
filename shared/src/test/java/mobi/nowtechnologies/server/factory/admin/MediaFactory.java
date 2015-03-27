/**
 *
 */

package mobi.nowtechnologies.server.factory.admin;

import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.enums.ItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mayboroda Dmytro
 */
public class MediaFactory {

    public static MediaDto anyMediaDto() {
        MediaDto expectedItemDto = new MediaDto();
        expectedItemDto.setId(Integer.MAX_VALUE);
        expectedItemDto.setType(ItemType.MEDIA);
        expectedItemDto.setIsrc("Some isrc");
        expectedItemDto.setTitle("Some title");
        expectedItemDto.setInfo("Some info");
        expectedItemDto.setArtistDto(anyArtistDto());

        return expectedItemDto;
    }

    public static ArtistDto anyArtistDto() {
        ArtistDto expectedItemDto = new ArtistDto();
        expectedItemDto.setId(Integer.MAX_VALUE);
        expectedItemDto.setInfo("Some Info");
        expectedItemDto.setName("Some name");
        expectedItemDto.setRealName("Some real name");

        return expectedItemDto;
    }

    /**
     * @return
     */
    public static List<MediaDto> getMediaDtos(int amount) {
        List<MediaDto> items = new ArrayList<MediaDto>();
        for (int i = 0; i < amount; i++) {
            items.add(anyMediaDto());
        }
        return items;
    }

    public static List<MediaDto> getMediaDtos(MediaDto item, int amount) {
        List<MediaDto> items = new ArrayList<MediaDto>();
        for (int i = 0; i < amount; i++) {
            items.add(item);
        }
        return items;
    }

    /**
     * @return
     */
    public static List<MediaDto> getEmptyMediaDtos() {
        return new ArrayList<MediaDto>();
    }
}
