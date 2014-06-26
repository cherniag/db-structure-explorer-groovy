package mobi.nowtechnologies.server.trackrepo.controller;

import com.google.common.collect.Sets;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Oleg Artomov on 6/25/2014.
 */
public class TrackControllerIT  extends AbstractTrackRepoITTest {

    @Resource
    private TrackRepository trackRepository;


    @Resource
    private FileRepository fileRepository;

    @Resource(name = "trackRepo.TrackService")
    private TrackService trackService;

    @Value("${trackRepo.encode.destination}")
    private org.springframework.core.io.Resource publishDir;


    @Test
    public void testEncode() throws Exception {
        AssetFileFactory assetFileFactory = new AssetFileFactory();
        assetFileFactory.setFileDir(publishDir.getFile());
        AssetFile image = assetFileFactory.anyAssetFile();
        fileRepository.save(image);
        AssetFile mp3Download = assetFileFactory.anyMp3DownloadFile();
        fileRepository.save(mp3Download);
        Track track = TrackFactory.anyTrack();
        track = trackRepository.saveAndFlush(track);
        track.setFiles(Sets.newHashSet(image, mp3Download));
        track = trackRepository.saveAndFlush(track);
        trackService.encode(track.getId(), false, false);
        track = trackRepository.findOne(track.getId());
        assertEquals(TrackStatus.ENCODED, track.getStatus());
    }



}
