package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
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
		this.setAmazonUrl(track.getAmazonUrl());
        this.setPublishDate(track.getPublishDate());

		this.setTerritoryCodes(track.getTerritoryCodes());
        this.setLabel(track.getLabel());
        this.setReleaseDate(track.getReleaseDate());
        this.setCoverFileName(track.getCoverFileId() != null ? track.getCoverFileId().toString() : "0");
        this.setMediaFileName(track.getMediaFileId() != null ? track.getMediaFileId().toString() : "0");
        this.setMediaType(track.getMediaType() != null ? FileType.valueOf(track.getMediaType().name()) : null);

        if(track.getFiles() != null){
            List<ResourceFileDto> files = new LinkedList<ResourceFileDto>();

            for (AssetFile file : track.getFiles()){
                if(file.getType() != null){
                    ResourceFileDto fileDto = new ResourceFileDto();

                    fileDto.setFilename(file.getPath());
                    fileDto.setType(file.getType().name());

                    files.add(fileDto);
                }
            }

            this.setFiles(files);
        }

        if(track.getTerritories() != null){
            List<TerritoryDto> territories = new LinkedList<TerritoryDto>();

            for (Territory territory : track.getTerritories()){
                TerritoryDto territoryDto = new TerritoryDto();

                territoryDto.setLabel(territory.getLabel());
                territoryDto.setCode(territory.getCode());
                territoryDto.setCreateDate(territory.getCreateDate());
                territoryDto.setCurrency(territory.getCurrency());
                territoryDto.setDealReference(territory.getDealReference());
                territoryDto.setDeleted(territory.isDeleted());
                territoryDto.setDeleteDate(territory.getDeleteDate());
                territoryDto.setDistributor(territory.getDistributor());
                territoryDto.setPrice(territory.getPrice());
                territoryDto.setPriceCode(territory.getPriceCode());
                territoryDto.setPublisher(territory.getPublisher());
                territoryDto.setStartDate(territory.getStartDate());
                territoryDto.setReportingId(territory.getReportingId());

                territories.add(territoryDto);
            }

            this.setTerritories(territories);
        }
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