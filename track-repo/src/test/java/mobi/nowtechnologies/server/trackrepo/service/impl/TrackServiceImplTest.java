/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.service.impl.CloudFileServiceImpl;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;

import java.util.Collections;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.Mock;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class TrackServiceImplTest {

    @Mock
    CloudFileServiceImpl cloudFileServiceMock;

    @Mock
    TrackRepository trackRepositoryMock;

    @InjectMocks
    TrackServiceImpl trackService;

    String srcPullContainer = "srcPullContainer";
    private String destPullContainer = "destPullContainer";

    @Before
    public void setUp() {
        initMocks(this);

        trackService.setSrcPullContainer(srcPullContainer);
        trackService.setDestPullContainer(destPullContainer);
    }

    @Test
    public void shouldNotPullAndReturnNullOWhenNoSuchTrackInDB() {
        //given
        long trackId = Long.MAX_VALUE;

        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(null);

        //when
        Track track = trackService.pull(trackId);

        //then
        assertNull(track);
    }

    @Test
    public void shouldNotPullAndReturnTrackAsIsWhenTrackStatusIsENCODING() {
        //given
        long trackId = Long.MAX_VALUE;

        Track trackMock = mock(Track.class);

        when(trackMock.getStatus()).thenReturn(TrackStatus.ENCODING);
        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(trackMock);

        //when
        Track track = trackService.pull(trackId);

        //then
        assertThat(track, is(trackMock));
    }

    @Test
    public void shouldNotPullAndReturnTrackAsIsWhenTrackStatusIsPUBLISHING() {
        //given
        long trackId = Long.MAX_VALUE;

        Track trackMock = mock(Track.class);

        when(trackMock.getStatus()).thenReturn(TrackStatus.PUBLISHING);
        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(trackMock);

        //when
        Track track = trackService.pull(trackId);

        //then
        assertThat(track, is(trackMock));
    }

    @Test
    public void shouldNotPullAndReturnTrackAsIsWhenTrackStatusIsNONE() {
        //given
        long trackId = Long.MAX_VALUE;

        Track trackMock = mock(Track.class);

        when(trackMock.getStatus()).thenReturn(TrackStatus.NONE);
        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(trackMock);

        //when
        Track track = trackService.pull(trackId);

        //then
        assertThat(track, is(trackMock));
    }

    @Test
    public void shouldReturnTrackWithTrackStatusENCODEDWhenSomeExceptionThrowsForTrackStatusIsENCODED() {
        //given
        long trackId = Long.MAX_VALUE;

        Track trackMock = mock(Track.class);

        when(trackMock.getId()).thenReturn(trackId);
        when(trackMock.getStatus()).thenReturn(TrackStatus.ENCODED);
        String uniqueTrackId = "uniqueTrackId";
        when(trackMock.getUniqueTrackId()).thenReturn(uniqueTrackId);

        AssetFile audioFileMock = mock(AssetFile.class);
        when(trackMock.getFile(AssetFile.FileType.DOWNLOAD)).thenReturn(audioFileMock);

        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(trackMock);

        String fileName = uniqueTrackId + "." + FileType.MOBILE_AUDIO.getExt();
        when(cloudFileServiceMock.copyFile(srcPullContainer, trackId + "_" + fileName, destPullContainer, fileName)).thenThrow(new RuntimeException());

        //when
        Track track = trackService.pull(trackId);

        //then
        assertThat(track, is(trackMock));
        assertThat(track.getStatus(), is(TrackStatus.ENCODED));
    }

    @Test
    public void shouldReturnTrackWithTrackStatusENCODEDWhenSomeExceptionThrowsForTrackStatusIsPUBLISHED() {
        //given
        long trackId = Long.MAX_VALUE;

        Track trackMock = mock(Track.class);

        when(trackMock.getId()).thenReturn(trackId);
        when(trackMock.getStatus()).thenReturn(TrackStatus.ENCODED);
        String uniqueTrackId = "uniqueTrackId";
        when(trackMock.getUniqueTrackId()).thenReturn(uniqueTrackId);

        AssetFile audioFileMock = mock(AssetFile.class);
        when(trackMock.getFile(AssetFile.FileType.DOWNLOAD)).thenReturn(audioFileMock);

        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(trackMock);

        String fileName = uniqueTrackId + "." + FileType.MOBILE_AUDIO.getExt();
        when(cloudFileServiceMock.copyFile(srcPullContainer, trackId + "_" + fileName, destPullContainer, fileName)).thenThrow(new RuntimeException());

        //when
        Track track = trackService.pull(trackId);

        //then
        assertThat(track, is(trackMock));
        assertThat(track.getStatus(), is(TrackStatus.ENCODED));
    }

    @Test
    public void shouldPull() {
        //given
        long trackId = Long.MAX_VALUE;

        AssetFile audioFile = new AssetFile();
        audioFile.setType(AssetFile.FileType.DOWNLOAD);

        Track track = new Track();
        track.setId(trackId);
        track.setStatus(TrackStatus.ENCODED);
        track.setIsrc("isrc");
        track.setFiles(Collections.singleton(audioFile));

        when(trackRepositoryMock.findOneWithCollections(trackId)).thenReturn(track);
        when(trackRepositoryMock.save(track)).thenReturn(track);

        ArgumentCaptor<String> srcFileNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> targetFileNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(cloudFileServiceMock.copyFile(eq(srcPullContainer), srcFileNameArgumentCaptor.capture(), eq(destPullContainer), targetFileNameArgumentCaptor.capture())).thenReturn(true);

        //when
        Track resultTrack = trackService.pull(trackId);

        //then
        assertThat(track, is(track));
        assertThat(resultTrack.getStatus(), is(TrackStatus.PUBLISHED));
    }
}