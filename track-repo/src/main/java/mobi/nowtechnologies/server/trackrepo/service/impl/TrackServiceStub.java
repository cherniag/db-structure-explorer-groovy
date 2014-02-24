package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 9/17/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrackServiceStub extends TrackServiceImpl {

    @Override
    public Track encode(Long trackId, Boolean isHighRate, Boolean licensed) {
        try {
            Thread.sleep(4000);

            Track track = trackRepository.findOneWithCollections(trackId);

            track.setStatus(TrackStatus.ENCODED);
            track.setResolution(isHighRate != null && isHighRate ? AudioResolution.RATE_96 : AudioResolution.RATE_48);
            track.setItunesUrl("http://www.apple.com/itunes/");

            trackRepository.save(track);

            LOGGER.info("output encode(trackId, isHighRate): [{}]", new Object[] { track });
            return track;
        } catch (InterruptedException e) {
            LOGGER.error("Cannot encode track files or create zip package.", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Track pull(Long trackId) {
        LOGGER.debug("input pull(trackId): [{}]", new Object[] { trackId });

        try {
            Thread.sleep(4000);

            Track track = trackRepository.findOneWithCollections(trackId);

            track.setStatus(TrackStatus.PUBLISHED);
            track.setResolution(AudioResolution.RATE_96);
            track.setPublishDate(new Date());

            LOGGER.info("output pull(trackId): [{}]", new Object[] { track });
            return track;
        } catch (InterruptedException e) {
            LOGGER.error("Cannot pull track.", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
