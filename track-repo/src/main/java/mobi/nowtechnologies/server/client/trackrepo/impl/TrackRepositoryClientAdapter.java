package mobi.nowtechnologies.server.client.trackrepo.impl;

import mobi.nowtechnologies.server.client.trackrepo.TrackRepositoryClient;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.shared.dto.TrackDto;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.service.TrackService;
import org.springframework.data.domain.Pageable;

public class TrackRepositoryClientAdapter implements TrackRepositoryClient {
    private TrackService trackService;

    public void setTrackService(TrackService trackService) {
        this.trackService = trackService;
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public PageListDto<TrackDto> search(String criteria, Pageable page) {
        return trackService.find(criteria, page);
    }

    @Override
    public TrackDto pullTrack(Long id) throws Exception {
        return trackService.pull(id);
    }

    @Override
    public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception {
        return trackService.encode(id, isHighRate, licensed);
    }

    @Override
    public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page) {
        return trackService.find(criteria, page);
    }
}
