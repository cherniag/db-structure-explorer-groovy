package mobi.nowtechnologies.server.trackrepo.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.controller.AbstractTrackRepoITTest;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Oleg Artomov on 6/25/2014.
 */
public class TrackServiceIT extends AbstractTrackRepoITTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TrackRepository trackRepository;


    @Resource
    private FileRepository fileRepository;

    @Resource(name = "trackRepo.TrackService")
    private TrackService trackService;

    @Value("${trackRepo.encode.destination}")
    private org.springframework.core.io.Resource publishDir;


    @Value("${trackRepo.pull.cdn.container.src}")
    private String privateContainerName;

    @Value("${trackRepo.pull.cdn.container.dest}")
    private String publicContainerName;


    @Resource
    private CloudFileService cloudFileService;

    private boolean isFileInCloudExists(String containerName, String fileName) {
        logger.info("Check file in cloud: {}", fileName);
        try {
            byte[] result = IOUtils.toByteArray(cloudFileService.getInputStream(containerName, fileName));
            return ArrayUtils.isNotEmpty(result);
        } catch (FilesNotFoundException e) {
                return false;
        } catch (IOException e) {
            return false;
        }
    }

    private void waitWhileCloudProcessCopying() throws InterruptedException {
        Thread.sleep(10000);
    }


    private Track encode() throws Exception {
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
        checkFilesNotExistInCloudBeforeEncoding(track);
        trackService.encode(track.getId(), false, false);
        track = trackRepository.findOne(track.getId());
        assertEquals(TrackStatus.ENCODED, track.getStatus());
        checkFilesExistsInCloudAfterEncoding(track);
        return track;
    }

    private Collection<String> getFilesAfterPull(Track track) {
        String prefix = track.getUniqueTrackId();
        List<String> result = Lists.newArrayList();
        result.add(prefix + ".aud");
        result.add(prefix + ".enc");
        result.add(prefix + "_21.jpg");
        result.add(prefix + "_22.jpg");
        result.add(prefix + "_48.aud");
        result.add(prefix + "_48.hdr");
        result.add(prefix + "_96.aud");
        result.add(prefix + "_96.hdr");
        return result;
    }

    private Collection<String> getFilesAfterEncoding(Track track) {
        String uniqueTrackId = track.getUniqueTrackId();
        String prefix = track.getId() + "_" + uniqueTrackId;
        List<String> result = Lists.newArrayList();
        result.add(prefix + "S.jpg");
        result.add(prefix + "_22.jpg");
        result.add(prefix + "_21.jpg");
        result.add(prefix + "_3.jpg");
        result.add(prefix + "_6.jpg");
        result.add(prefix + "_cover.png");
        result.add(prefix + ".mp3");
        result.add(prefix + "_48.aud");
        result.add(prefix + "_48.hdr");
        result.add(prefix + "_48.enc");
        result.add(prefix + ".aud");
        result.add(prefix + ".enc");
        result.add(prefix + "_96.aud");
        result.add(prefix + "_96.hdr");
        result.add(prefix + "_96.enc");
        return result;
    }

    private void checkFilesExistsInCloudAfterEncoding(Track track) {
        Collection<String> filesInCloud = getFilesAfterEncoding(track);
        for (String currentFile : filesInCloud) {
            assertTrue(isFileInCloudExists(privateContainerName, currentFile));
        }
    }

    private void checkFilesNotExistInCloudBeforeEncoding(Track track) {
        Collection<String> filesInCloud = getFilesAfterEncoding(track);
        for (String currentFile : filesInCloud) {
            assertFalse(isFileInCloudExists(privateContainerName, currentFile));
        }
    }


    private void checkFilesNotExistsInCloudBeforePull(Track track) {
        Collection<String> filesInCloud = getFilesAfterPull(track);
        for (String currentFile : filesInCloud) {
            assertFalse(isFileInCloudExists(publicContainerName, currentFile));
        }
    }


    private void checkFilesExistsInCloudAfterPull(Track track) {
        Collection<String> filesInCloud = getFilesAfterPull(track);
        for (String currentFile : filesInCloud) {
            assertTrue(isFileInCloudExists(publicContainerName, currentFile));
        }
    }


    @Test
    public void testEncode() throws Exception {
        encode();
    }


    @Test
    public void testPull() throws Exception {
        Track resultEncoding = encode();
        checkFilesNotExistsInCloudBeforePull(resultEncoding);
        trackService.pull(resultEncoding.getId());
        long curTime = System.currentTimeMillis();
        Track track = trackRepository.findOne(resultEncoding.getId());
        assertEquals(TrackStatus.PUBLISHED, track.getStatus());
        assertNotNull(track.getPublishDate());
        assertEquals(curTime-curTime%100000, track.getPublishDate().getTime()-track.getPublishDate().getTime()%100000);
        waitWhileCloudProcessCopying();
        checkFilesExistsInCloudAfterPull(resultEncoding);
    }


    @Test
    public void testSearchByTrackId() throws Exception {
        Track prepared = prepareTrackForSearch();
        SearchTrackDto criteria = new SearchTrackDto();
        criteria.setTrackIds(Lists.newArrayList(prepared.getId().intValue()));
        PageRequest request = new PageRequest(0, 1);
        Page<Track> result =  trackService.find(criteria, request);
        assertEquals(result.getNumberOfElements(), 1);
        Track track = trackRepository.findOne(prepared.getId());
        assertEquals(result.getContent().get(0), track);
    }

    private Track prepareTrackForSearch() {
        Track track = TrackFactory.anyTrack();
        return trackRepository.save(track);
    }


    @Before
    public void beforeEachTest(){
        assertFalse(privateContainerName.contentEquals(publicContainerName));
    }

}
