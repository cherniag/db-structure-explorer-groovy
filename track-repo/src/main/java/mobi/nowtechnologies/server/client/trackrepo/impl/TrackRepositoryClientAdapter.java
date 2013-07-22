package mobi.nowtechnologies.server.client.trackrepo.impl;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryClient;
import mobi.nowtechnologies.server.trackrepo.controller.IngestTracksWizardController;
import mobi.nowtechnologies.server.trackrepo.controller.TrackController;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.springframework.data.domain.Pageable;

public class TrackRepositoryClientAdapter implements TrackRepositoryClient {
    private TrackController trackController;

    private IngestTracksWizardController ingestTracksWizardController;


    public void setTrackController(TrackController trackController) {
        this.trackController = trackController;
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public PageListDto<TrackDto> search(String criteria, Pageable page) {
        return (PageListDto<TrackDto>)trackController.find(criteria, null,  page);
    }

    @Override
    public TrackDto pullTrack(Long id) throws Exception {
        return trackController.pull(id);
    }

    @Override
    public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception {
        return new TrackDto(trackController.encode(id, isHighRate, licensed));
    }

    @Override
    public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page) {
        return (PageListDto<TrackDto>)trackController.find(null, criteria, page);
    }

    @Override
    public IngestWizardDataDto getDrops(){
        try {
            return ingestTracksWizardController.getDrops();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IngestWizardDataDto selectDrops(IngestWizardDataDto data) {
        try {
            return ingestTracksWizardController.selectDrops(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IngestWizardDataDto selectTrackDrops(IngestWizardDataDto data){
        try {
            return ingestTracksWizardController.selectDropTracks(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean commitDrops(IngestWizardDataDto data) {
        try {
            return ingestTracksWizardController.commitDrops(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
