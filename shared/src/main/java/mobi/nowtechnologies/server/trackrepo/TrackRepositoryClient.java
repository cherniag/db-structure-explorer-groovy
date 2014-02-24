package mobi.nowtechnologies.server.trackrepo;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * @author Mayboroda Dmytro
 *
 */
public interface TrackRepositoryClient {
	
	/**
	 * Checks if client may connect to track repository with provided credentials
	 * @return true if client is logged in false otherwise
	 */
	public boolean isLoggedIn();
	
	/**
	 * Searches for tracks in tack repository by criteria
	 * @param criteria
	 * @param page pagination parameters
	 * @return
	 */
	public PageListDto<TrackDto> search(String criteria, Pageable page);
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public TrackDto pullTrack(Long id) throws Exception;
	
	/**
	 * Encode track
	 * @param id track id
	 * @param licensed 
	 * @param isHighRate 
	 * @return TrackDto object if http status code is OK or CREATED and null if NO_CONTENT  
	 * @throws Exception if some exception occurred or wrong response code
	 */
	public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception;

	/**
	 * Searches for tracks in tack repository by criteria
	 * @param criteria
	 * @param page pagination parameters
	 * @return
	 */
	public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page);

    /*
        * (non-Javadoc)
        *
        * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#search (java.lang.String)
        */
    IngestWizardDataDto getDrops(String... ingestors);

    /*
        * (non-Javadoc)
        *
        * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#getDrops (java.lang.String)
        */
    IngestWizardDataDto selectDrops(IngestWizardDataDto data);

    /*
        * (non-Javadoc)
        *
        * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#selectTrackDrops (java.lang.String)
        */
    IngestWizardDataDto selectTrackDrops(IngestWizardDataDto data);

    /*
        * (non-Javadoc)
        *
        * @see mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient#selectTrackDrops (java.lang.String)
        */
    Boolean commitDrops(IngestWizardDataDto data);
}