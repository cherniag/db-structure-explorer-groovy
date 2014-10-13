package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.ApplicationPageData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest(OrdinalBlockDto.class)
@RunWith(PowerMockRunner.class)
public class OrdinalBlockDtoTest {

    @Mock ApplicationPageData applicationPageDataMock;
    @Mock PlaylistData playlistDataMock;
    @Mock TrackData trackDataMock;

    OrdinalBlockDto ordinalBlockDto;

    @Test
    public void shouldGetValueOpenerForExternalAd() throws Exception {
        //given
        ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setKey(LinkLocationType.EXTERNAL_AD.name());
        ordinalBlockDto.setValue("value");

        PowerMockito.mock(ApplicationPageData.class);
        whenNew(ApplicationPageData.class).withArguments(ordinalBlockDto.getValue()).thenReturn(applicationPageDataMock);
        String opener = "opener";
        PowerMockito.doReturn(opener).when(applicationPageDataMock).getAction();

        //when
        String valueOpener = ordinalBlockDto.getValueOpener();

        //then
        assertThat(valueOpener, is(opener));
    }

    @Test
    public void shouldGetValueOpenerForInternalAd() throws Exception {
        //given
        ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setKey(LinkLocationType.INTERNAL_AD.name());
        ordinalBlockDto.setValue("value");

        PowerMockito.mock(ApplicationPageData.class);
        whenNew(ApplicationPageData.class).withArguments(ordinalBlockDto.getValue()).thenReturn(applicationPageDataMock);
        String opener = "opener";
        PowerMockito.doReturn(opener).when(applicationPageDataMock).getAction();

        //when
        String valueOpener = ordinalBlockDto.getValueOpener();

        //then
        assertThat(valueOpener, is(opener));
    }

    @Test
    public void shouldGetValuePlayerTypeForPlaylist() throws Exception {
        //given
        ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setKey(MusicType.PLAYLIST.name());
        ordinalBlockDto.setValue("value");

        PowerMockito.mock(PlaylistData.class);
        whenNew(PlaylistData.class).withArguments(ordinalBlockDto.getValue()).thenReturn(playlistDataMock);
        String playerType = "playerType";
        PowerMockito.doReturn(playerType).when(playlistDataMock).getPlayerTypeString();

        //when
        String valuePlayerType = ordinalBlockDto.getValuePlayerType();

        //then
        assertThat(valuePlayerType, is(playerType));
    }

    @Test
    public void shouldGetValuePlayerTypeForTrack() throws Exception {
        //given
        ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setKey(MusicType.TRACK.name());
        ordinalBlockDto.setValue("value");

        PowerMockito.mock(TrackData.class);
        whenNew(TrackData.class).withArguments(ordinalBlockDto.getValue()).thenReturn(trackDataMock);
        String playerType = "playerType";
        PowerMockito.doReturn(playerType).when(trackDataMock).getPlayerTypeString();

        //when
        String valuePlayerType = ordinalBlockDto.getValuePlayerType();

        //then
        assertThat(valuePlayerType, is(playerType));
    }
}