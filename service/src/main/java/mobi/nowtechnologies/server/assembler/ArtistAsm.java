package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ArtistAsm {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtistAsm.class);
	
	public static ArtistDto toArtistDto(Artist artist) {
		LOGGER.debug("input parameters artist: [{}]", artist);
		
		ArtistDto artistDto = new ArtistDto();
		
		artistDto.setId(artist.getI());
		artistDto.setInfo(artist.getInfo());
		artistDto.setName(artist.getName());
		artistDto.setRealName(artist.getRealName());
		
		LOGGER.info("Output parameter artistDto=[{}]", artistDto);
		return artistDto;
	}

}
