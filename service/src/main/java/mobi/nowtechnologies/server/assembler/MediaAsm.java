package mobi.nowtechnologies.server.assembler;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.enums.CurrencyCode;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class MediaAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaAsm.class);
	
	public static MediaDto toMediaDto(Media media) {
		//LOGGER.debug("input parameters media: [{}]", media);
		
		MediaDto mediaDto = new MediaDto();
		
		mediaDto.setId(media.getI());
		mediaDto.setArtistDto(ArtistAsm.toArtistDto(media.getArtist()));
		//mediaDto.setAudioFileDto(MediaFileAsm.toMediaFileDto(media.getAudioFile()));
		//mediaDto.setAudioPreviewFileDto(MediaFileAsm.toMediaFileDto(media.getAudioPreviewFile()));
		//mediaDto.setHeaderFileDto(MediaFileAsm.toMediaFileDto(media.getHeaderFile()));
		//mediaDto.setHeaderPreviewFileDto(MediaFileAsm.toMediaFileDto(media.getHeaderPreviewFile()));
		//mediaDto.setImageFIleLargeDto(MediaFileAsm.toMediaFileDto(media.getImageFIleLarge()));
		mediaDto.setImageFileSmallDto(MediaFileAsm.toMediaFileDto(media.getImageFileSmall()));
		//mediaDto.setImgFileResolutionDto(MediaFileAsm.toMediaFileDto(media.getImgFileResolution()));
		mediaDto.setInfo(media.getInfo());
		mediaDto.setIsrc(media.getIsrc());
		mediaDto.setITunesUrl(media.getiTunesUrl());
		mediaDto.setPriceCurrency(media.getPrice_currency());
		//mediaDto.setLabel(media.getLabel());
		mediaDto.setPublishDate(new Date(media.getPublishDate()*1000L));
		//mediaDto.setPurchasedFileDto(MediaFileAsm.toMediaFileDto(media.getPurchasedFile()));
		mediaDto.setTitle(media.getTitle());
		mediaDto.setPrice(media.getPrice());
		mediaDto.setType(media.getType());
		
		LOGGER.info("Output parameter mediaDto=[{}]", mediaDto);
		return mediaDto;
	}
	
	@SuppressWarnings("unchecked")
	public static List<MediaDto> toMediaDtos(List<Media> medias) {
		//LOGGER.debug("input parameters medias: [{}]", medias);
		
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
