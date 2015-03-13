package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType.MINI_PLAYER_ONLY;

import static java.util.Collections.singletonList;

import org.springframework.context.MessageSource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import static org.hamcrest.core.Is.is;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest(StreamzineUpdateAdminAsm.class)
@RunWith(PowerMockRunner.class)
public class StreamzineUpdateAdminAsmTest {

    @Mock
    MessageSource messageSourceMock;
    @Mock
    StreamzineAdminMediaAsm streamzineAdminMediaAsmMock;

    @InjectMocks
    StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldConvertPlayListBlockValue() throws Exception {
        Update updateMock = mock(Update.class);
        Community communityMock = mock(Community.class);
        Block blockMock = mock(Block.class);
        doReturn(null).when(blockMock).getBadgeId();
        MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfoMock = mock(MusicPlayListDeeplinkInfo.class);

        doReturn(musicPlayListDeeplinkInfoMock).when(blockMock).getDeeplinkInfo();
        doReturn(singletonList(blockMock)).when(updateMock).getBlocks();

        PlaylistData playlistDataMock = mock(PlaylistData.class);
        whenNew(PlaylistData.class).withArguments(musicPlayListDeeplinkInfoMock.getChartId(), musicPlayListDeeplinkInfoMock.getPlayerType()).thenReturn(playlistDataMock);
        String valueString = "";
        doReturn(valueString).when(playlistDataMock).toValueString();

        doReturn(null).when(streamzineAdminMediaAsmMock).toPlaylistDto(musicPlayListDeeplinkInfoMock, communityMock);

        //when
        UpdateDto updateDto = streamzineUpdateAdminAsm.convertOneWithBlocksToIncoming(updateMock, communityMock);

        //then
        assertNotNull(updateDto);
        assertThat(((OrdinalBlockDto) updateDto.getBlocks().iterator().next()).getValue(), is(valueString));
    }

    @Test
    public void shouldConvertTrackBlockValue() throws Exception {
        Update updateMock = mock(Update.class);
        Community communityMock = mock(Community.class);
        Block blockMock = mock(Block.class);
        doReturn(null).when(blockMock).getBadgeId();
        MusicTrackDeeplinkInfo musicTrackDeeplinkInfoMock = mock(MusicTrackDeeplinkInfo.class);

        doReturn(musicTrackDeeplinkInfoMock).when(blockMock).getDeeplinkInfo();
        doReturn(singletonList(blockMock)).when(updateMock).getBlocks();

        Media mediaMock = mock(Media.class);

        doReturn(mediaMock).when(musicTrackDeeplinkInfoMock).getMedia();
        doReturn(MINI_PLAYER_ONLY).when(musicTrackDeeplinkInfoMock).getPlayerType();

        TrackData trackDataMock = mock(TrackData.class);
        whenNew(TrackData.class).withArguments(musicTrackDeeplinkInfoMock.getMedia(), musicTrackDeeplinkInfoMock.getPlayerType()).thenReturn(trackDataMock);
        String valueString = "";
        doReturn(valueString).when(trackDataMock).toValueString();

        //when
        UpdateDto updateDto = streamzineUpdateAdminAsm.convertOneWithBlocksToIncoming(updateMock, communityMock);

        //then
        assertNotNull(updateDto);
        assertThat(((OrdinalBlockDto) updateDto.getBlocks().iterator().next()).getValue(), is(valueString));
    }
}