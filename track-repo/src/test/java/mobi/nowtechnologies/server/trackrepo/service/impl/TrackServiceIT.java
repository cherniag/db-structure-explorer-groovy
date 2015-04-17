package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.controller.AbstractTrackRepoIT;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.junit.*;
import static org.junit.Assert.*;

// Created by Oleg Artomov on 6/25/2014.
@Ignore
public class TrackServiceIT extends AbstractTrackRepoIT {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    TrackRepository trackRepository;

    @Resource(name = "trackRepo.TrackService")
    TrackService trackService;

    @Test
    public void testSearchByTrackId() throws Exception {
        Track prepared = trackRepository.save(TrackFactory.anyTrack());
        SearchTrackDto criteria = new SearchTrackDto();

        criteria.setTrackIds(Lists.newArrayList(prepared.getId().intValue()));
        PageRequest request = new PageRequest(0, 1);

        Page<Track> result = trackService.find(criteria, request);
        assertEquals(result.getNumberOfElements(), 1);

        Track track = trackRepository.findOne(prepared.getId());
        assertEquals(result.getContent().get(0), track);
    }

}
