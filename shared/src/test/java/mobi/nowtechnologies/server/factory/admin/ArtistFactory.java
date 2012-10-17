/**
 * 
 */
package mobi.nowtechnologies.server.factory.admin;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ArtistFactory {
	
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
	public static List<ArtistDto> getArtistDtos(int amount) {
		List<ArtistDto> items = new ArrayList<ArtistDto>();
		for (int i=0; i<amount; i++)
			items.add(anyArtistDto());
		return items;
	}
	
	public static List<ArtistDto> getArtistDtos(ArtistDto item, int amount) {
		List<ArtistDto> items = new ArrayList<ArtistDto>();
		for (int i=0; i<amount; i++)
			items.add(item);
		return items;
	}

	/**
	 * @return
	 */
	public static List<ArtistDto> getEmptyArtistDtos() {
		return new ArrayList<ArtistDto>();
	}
}
