package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import org.springframework.data.domain.Page;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackDtoMapper extends mobi.nowtechnologies.server.trackrepo.dto.TrackDto {
	
	public TrackDtoMapper(Track track) {
		this.setId(track.getId());
		this.setTitle(track.getTitle());
        this.setExplicit(track.getExplicit());
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

		this.setTerritories(track.getTerritoryCodes());
        this.setLabel(track.getLabel());
        this.setReleaseDate(track.getReleaseDate());
        this.setCoverFileName(track.getCoverFileId() != null ? track.getCoverFileId().toString() : "0");
        this.setMediaFileName(track.getMediaFileId() != null ? track.getMediaFileId().toString() : "0");
	}
	
	public static List<TrackDtoMapper> toList(List<Track> tracks) {
		List<TrackDtoMapper> trackDtos = new LinkedList<TrackDtoMapper>();

		for (Track track : tracks) {
			trackDtos.add(new TrackDtoMapper(track));
		}

		return trackDtos;
	}
	
	public static PageListDto<TrackDtoMapper> toPage(Page<Track> tracks) {
		long total = tracks.getTotalElements();
		total = total % tracks.getSize() == 0 ? total / tracks.getSize() : total / tracks.getSize() + 1;

		return new PageListDto<TrackDtoMapper>(toList(tracks.getContent()), (int)total, tracks.getNumber(), tracks.getSize());
	}
}