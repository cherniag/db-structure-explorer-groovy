package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.NegativeTag;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

// @author Titov Mykhaylo (titov) on 10.11.2014.
public class TrackReportingOptionsService {

    private TrackRepository trackRepository;

    public void setTrackRepository(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Transactional
    public Track assignReportingOptions(TrackReportingOptionsDto trackReportingOptionsDto){
        Track track = trackRepository.findOne(trackReportingOptionsDto.getTrackId());

        track.setReportingType(trackReportingOptionsDto.getReportingType());
        track.assignNegativeTags(trackReportingOptionsDto.getNegativeTags());

        return trackRepository.save(track);
    }
}
