package mobi.nowtechnologies.server.trackrepo.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;

import org.springframework.data.domain.Page;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackDtoExt extends mobi.nowtechnologies.server.trackrepo.dto.TrackDto {
	
	public TrackDtoExt(Track track) {
		this.setId(track.getId());
		this.setTitle(track.getTitle());
		this.setAlbum(track.getAlbum());
		this.setArtist(track.getArtist());
		this.setCopyright(track.getCopyright());
		this.setStatus(track.getStatus());
		this.setGenre(track.getGenre());
		this.setInfo(track.getInfo());
		this.setIngestor(track.getIngestor());
		this.setIsrc(track.getIsrc());
		this.setProductCode(track.getProductCode());
		this.setProductId(track.getProductId());
		this.setSubTitle(track.getSubTitle());
		this.setYear(track.getYear());
		this.setIngestionDate(track.getIngestionDate());
		this.setIngestionUpdateDate(track.getIngestionUpdateDate());
		this.setLicensed(track.getLicensed());
		this.setResolution(track.getResolution());
		this.setItunesUrl(track.getItunesUrl());

		StringBuilder territories = new StringBuilder();
		Date releaseDate = null;
		String label = null;
		if (track.getTerritories() != null) {
			for (Territory territory : track.getTerritories())
			{
				if (territories.length() > 0){
					territories.append(", ");
				}else{
					releaseDate = territory.getStartDate();
					label = territory.getLabel();
				}
				territories.append(territory.getCode());
			}
		}
		this.setLabel(label);
		this.setReleaseDate(releaseDate);
		this.setTerritories(territories.toString());

		Long fileId = track.getFileId(AssetFile.FileType.IMAGE);
		this.setCoverFileName(fileId != null ? fileId.toString() : "0");
	}
	
	public static List<TrackDtoExt> toList(List<Track> tracks) {
		List<TrackDtoExt> trackDtos = new LinkedList<TrackDtoExt>();

		for (Track track : tracks) {
			trackDtos.add(new TrackDtoExt(track));
		}

		return trackDtos;
	}
	
	public static PageListDto<TrackDtoExt> toPage(Page<Track> tracks) {
		long total = tracks.getTotalElements();
		total = total % tracks.getSize() == 0 ? total / tracks.getSize() : total / tracks.getSize() + 1;

		return new PageListDto<TrackDtoExt>(toList(tracks.getContent()), (int)total, tracks.getNumber(), tracks.getSize());
	}
}