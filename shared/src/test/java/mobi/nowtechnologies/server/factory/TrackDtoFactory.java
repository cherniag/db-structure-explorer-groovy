/**
 * 
 */
package mobi.nowtechnologies.server.factory;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.shared.dto.*;

/**
 * @author Mayboroda Dmytro
 *
 */
public class TrackDtoFactory {
	
	public static TrackDto anyTrackDto() {
		TrackDto expectedTrackDto = new TrackDto();
		expectedTrackDto.setId(Long.MAX_VALUE);
		expectedTrackDto.setIngestor(null);
		expectedTrackDto.setIsrc(null);
		expectedTrackDto.setTitle(null);
		expectedTrackDto.setArtist(null);
		expectedTrackDto.setIngestionDate(null);
		expectedTrackDto.setStatus(TrackStatus.NONE);
		expectedTrackDto.setSubTitle(null);
		expectedTrackDto.setProductId(null);
		expectedTrackDto.setProductCode(null);
		expectedTrackDto.setGenre("Pop");
		expectedTrackDto.setCopyright("(P) 2010 Paul Simon under exclusive license of Sony Music Entertainment");
		expectedTrackDto.setYear(null);
		expectedTrackDto.setAlbum("Hearts And Bones");
		expectedTrackDto.setInfo(null);
		expectedTrackDto.setLicensed(true);
		expectedTrackDto.setIngestionUpdateDate(null);
		expectedTrackDto.setPublishDate(null);
		expectedTrackDto.setFiles(null);
		expectedTrackDto.setResolution(AudioResolution.RATE_48);
		return expectedTrackDto;
	}
	
	/**
	 * @return
	 */
	public static List<TrackDto> getTrackDtos(int amount) {
		List<TrackDto> tracks = new ArrayList<TrackDto>();
		for (int i=0; i<amount; i++)
			tracks.add(anyTrackDto());
		return tracks;
	}
	
	public static List<TrackDto> getTrackDtos(TrackDto sampleTrack, int amount) {
		List<TrackDto> tracks = new ArrayList<TrackDto>();
		for (int i=0; i<amount; i++)
			tracks.add(sampleTrack);
		return tracks;
	}
	
	public static PageListDto<TrackDto> getTrackPage(int amount) {
		return new PageListDto<TrackDto>(getTrackDtos(amount), 10, 1, amount);
	}
	
	public static PageListDto<TrackDto> getTrackPage(TrackDto sampleTrack, int amount) {
		return new PageListDto<TrackDto>(getTrackDtos(sampleTrack, amount), 10, 1, amount);
	}

	/**
	 * @return
	 */
	public static List<TrackDto> getEmptyTrackDtos() {
		return new ArrayList<TrackDto>();
	}

	public static PageListDto<TrackDto> getEmptyTrackPage() {
		return new PageListDto<TrackDto>(new ArrayList<TrackDto>(), 0, 1, 0);
	}
}
