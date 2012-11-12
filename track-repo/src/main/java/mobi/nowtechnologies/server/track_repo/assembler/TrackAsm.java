package mobi.nowtechnologies.server.track_repo.assembler;

import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.shared.dto.TrackDto;
import mobi.nowtechnologies.server.track_repo.domain.AssetFile;
import mobi.nowtechnologies.server.track_repo.domain.Territory;
import mobi.nowtechnologies.server.track_repo.domain.Track;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackAsm {
	public static List<TrackDto> toTrackDtos(List<Track> tracks) {
		List<TrackDto> TrackDtos = new LinkedList<TrackDto>();

		for (Track track : tracks) {
			TrackDtos.add(toTrackDto(track));
		}

		return TrackDtos;
	}

	public static TrackDto toTrackDto(Track track) {
		TrackDto trackDto = new TrackDto();

		trackDto.setId(track.getId());
		trackDto.setTitle(track.getTitle());
		trackDto.setAlbum(track.getAlbum());
		trackDto.setArtist(track.getArtist());
		trackDto.setCopyright(track.getCopyright());
		trackDto.setStatus(track.getStatus());
		trackDto.setGenre(track.getGenre());
		trackDto.setInfo(track.getInfo());
		trackDto.setIngestor(track.getIngestor());
		trackDto.setIsrc(track.getIsrc());
		trackDto.setProductCode(track.getProductCode());
		trackDto.setProductId(track.getProductId());
		trackDto.setSubTitle(track.getSubTitle());
		trackDto.setYear(track.getYear());
		trackDto.setIngestionDate(track.getIngestionDate());
		trackDto.setIngestionUpdateDate(track.getIngestionUpdateDate());
		trackDto.setLicensed(track.getLicensed());
		trackDto.setResolution(track.getResolution());
		trackDto.setItunesUrl(track.getItunesUrl());
		
		StringBuilder territories = new StringBuilder();
		if(track.getTerritories() != null){
			for(Territory territory : track.getTerritories())
			{
				if(territories.length() > 0)
					territories.append(", ");
				territories.append(territory.getCode());
			}
		}
		trackDto.setTerritories(territories.toString());

		Long fileId = track.getFileId(AssetFile.FileType.IMAGE);
		trackDto.setCoverFileName(fileId != null ? fileId.toString() : "0");

		return trackDto;
	}
}