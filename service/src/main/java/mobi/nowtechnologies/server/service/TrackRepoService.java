package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;

import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

// @author Mayboroda Dmytro
public interface TrackRepoService {
	
	/**
	 * Returns a list of tracks from track repository by search criteria
	 * @param criteria
	 * @return matched track list
	 */
	public PageListDto<TrackDto> find(String criteria, Pageable page);
	
	/**
	 * Returns a list of tracks from track repository by track proprties
	 * @return matched track list
	 */
	public PageListDto<TrackDto> find(SearchTrackDto criteria, Pageable page);
	
	/**
	 * Retrieves a track info from track repository by id and store it to tb_media and tb_files
	 * @return track that was published in database
	 */
	public TrackDto pull(TrackDto track);
	
	/**
	 * Starts encoding process on track repository for track with specified id
	 * @return track representing a track that is in encoding process right now
	 */
	public TrackDto encode(TrackDto track);

    /**
     * Scans all drops of file system and checks, indexes them and then returns.
     *
     * @return IngestWizardData drop data without drop tracks
     */
    IngestWizardDataDto getDrops(String... ingestors);

    /**
     * Select drops which needs to commit.
     *
     * @param data data about selected drops.
     */
    IngestWizardDataDto selectDrops(IngestWizardDataDto data);

    /**
     * Select drop tracks which needs to commit.
     *
     * @param data data about selected drop tracks.
     */
    IngestWizardDataDto selectTrackDrops(IngestWizardDataDto data);

    /**
     * Commit all selected tracks to database like indexed tracks with all their assert files, and then mark them like ingested.
     *
     * @param data data about selected drops.
     *
     * @return whether tracks saved to database or not
     */
    Boolean commitDrops(IngestWizardDataDto data);

    public Map<String, List<TrackDto>> encodeTracks(List<TrackDto> tracks);

    void assignReportingOptions(TrackReportingOptionsDto trackReportingOptionsDto);
}