package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static mobi.nowtechnologies.server.assembler.ArtistAsm.toArtistDto;
import static mobi.nowtechnologies.server.assembler.MediaFileAsm.toMediaFileDto;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MediaAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaAsm.class);
	
	public static MediaDto toMediaDto(Media media) {
		LOGGER.debug("input parameters media: [{}]", media);
		
		MediaDto mediaDto = new MediaDto();
		
		mediaDto.setId(media.getI());
		mediaDto.setArtistDto(toArtistDto(media.getArtist()));
		mediaDto.setAudioFileDto(toMediaFileDto(media.getAudioFile()));
		mediaDto.setImageFileSmallDto(toMediaFileDto(media.getImageFileSmall()));
		mediaDto.setInfo(media.getInfo());
		mediaDto.setIsrc(media.getIsrc());
		mediaDto.setITunesUrl(media.getiTunesUrl());
		mediaDto.setPriceCurrency(media.getPrice_currency());

        Label label = media.getLabel();
        if (isNotNull(label))
            mediaDto.setLabel(label.getName());
        mediaDto.setTrackId(media.getIsrcTrackId());
		mediaDto.setPublishDate(new Date(media.getPublishDate()*1000L));
		mediaDto.setTitle(media.getTitle());
		mediaDto.setPrice(media.getPrice());
		mediaDto.setType(media.getType());
		
		LOGGER.info("Output parameter mediaDto=[{}]", mediaDto);
		return mediaDto;
	}
	
	@SuppressWarnings("unchecked")
	public static List<MediaDto> toMediaDtos(List<Media> medias) {
		LOGGER.debug("input parameters medias: [{}]", medias);
		
		List<MediaDto> mediaDtos;
		if(medias.isEmpty()){
			mediaDtos = Collections.EMPTY_LIST;
		}else{
			mediaDtos = new LinkedList<MediaDto>();
			
			for (Media media : medias) {
				mediaDtos.add(toMediaDto(media));				
			}
		}
		
		LOGGER.info("Output parameter mediaDtos=[{}]", mediaDtos);
		return mediaDtos;
		
	}

}
