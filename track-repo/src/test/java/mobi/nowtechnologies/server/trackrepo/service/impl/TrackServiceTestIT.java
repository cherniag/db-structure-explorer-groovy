package mobi.nowtechnologies.server.trackrepo.service.impl;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.controller.AbstractTrackRepoITTest;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TrackServiceTestIT extends AbstractTrackRepoITTest{
	private final String DEFAULT_FILE_NAME = "work/APPCASTP.m4a";

	@Resource(name = "trackRepo.TrackServiceStub")
	private TrackServiceImpl trackService;


    @Resource(name = "trackRepo.TrackService")
    private TrackServiceImpl trackServiceWithDB;


    @Resource
	private TrackRepository trackRepository;

    @Resource
	private CloudFileService cloudFileService;

    @Test
    public void testSearchByTrackId() throws Exception {
        Track prepared = prepareTrackForSearch();
        SearchTrackDto criteria = new SearchTrackDto();
        criteria.setTrackIds(Lists.newArrayList(prepared.getId().intValue()));
        PageRequest request = new PageRequest(0, 1);
        Page<Track> result =  trackServiceWithDB.find(criteria, request);
        assertEquals(result.getNumberOfElements(), 1);
        Track track = trackRepository.findOne(prepared.getId());
        assertEquals(result.getContent().get(0), track);
    }


    @Test
	public void testPull_Success() throws Exception {
		//test preparation
		Track anyTrack = TrackFactory.anyTrack();
		anyTrack.setStatus(TrackStatus.ENCODED);
        anyTrack.setMediaType(AssetFile.FileType.MOBILE);
		anyTrack = trackRepository.save(anyTrack);
		
		String isrc = anyTrack.getIsrc();
		Long trackId = anyTrack.getId();
		
		MultipartFile audioFile = createTestFile(trackId+"_"+isrc+"."+FileType.MOBILE_AUDIO.getExt());		
		MultipartFile encodedFile = createTestFile(trackId+"_"+isrc+"."+FileType.MOBILE_ENCODED.getExt());
		MultipartFile largeImageFile = createTestFile(trackId+"_"+isrc+ImageResolution.SIZE_22.getSuffix()+"."+FileType.IMAGE.getExt());
		MultipartFile smallImageFile = createTestFile(trackId+"_"+isrc+ImageResolution.SIZE_21.getSuffix()+"."+FileType.IMAGE.getExt());
		
		cloudFileService.uploadFile(audioFile, audioFile.getName());
		cloudFileService.uploadFile(encodedFile, encodedFile.getName());
		cloudFileService.uploadFile(largeImageFile, largeImageFile.getName());
		cloudFileService.uploadFile(smallImageFile, smallImageFile.getName());
		
		//call test method
		Track track = trackService.pull(anyTrack.getId());
		
		//assertion
		long curTime = System.currentTimeMillis();
		assertNotNull(track);
		assertEquals(anyTrack.getId(), track.getId());
        assertEquals(TrackStatus.PUBLISHED, track.getStatus());
		assertNotNull(track.getPublishDate());
		assertEquals(curTime-curTime%100000, track.getPublishDate().getTime()-track.getPublishDate().getTime()%100000);
	}

    @Ignore
    @Test
    public void testCreateVideo_Success() throws Exception {
        //test preparation
        URL videoURL = this.getClass().getClassLoader().getResource("media/manual/020313/o2Tracks.mp4");
        String videoPath = new File(videoURL.toURI()).getAbsolutePath();

        Track anyTrack = TrackFactory.anyTrack();
        anyTrack.setIsrc("US-UM7-11-00061");
        anyTrack.setStatus(TrackStatus.ENCODED);
        AssetFile videoFile = new AssetFile();
        videoFile.setType(AssetFile.FileType.VIDEO);
        videoFile.setPath(videoPath);
        anyTrack.setFiles(Collections.singleton(videoFile));

        //call test method
        AssetFile result = trackService.createVideo(anyTrack);

        //assertion
        assertNotNull(result.getExternalId());
    }
	
	private MultipartFile createTestFile(String fileName) throws IOException{
		InputStream srcFile = getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME);
		FileItem fileItem = new DiskFileItemFactory().createItem(fileName, "application/octet-stream", true, fileName);
		IOUtils.copy(srcFile, fileItem.getOutputStream());
		MultipartFile file = new CommonsMultipartFile(fileItem); 
		
		return file;
	}

    private Track prepareTrackForSearch() {
        Track track = TrackFactory.anyTrack();
        return trackRepository.save(track);
    }

}