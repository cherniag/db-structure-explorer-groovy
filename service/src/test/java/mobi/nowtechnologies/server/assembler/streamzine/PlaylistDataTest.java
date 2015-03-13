package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import static mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData.TOKEN;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType.MINI_PLAYER_ONLY;

import com.google.common.base.Joiner;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PlaylistDataTest {

    PlaylistData playlistData;

    @Test(expected = NullPointerException.class)
    public void shouldNotConstruct() {
        playlistData = new PlaylistData(Integer.MAX_VALUE, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotConstructFromNullString() {
        playlistData = new PlaylistData(null);
    }

    public void shouldConstructFromStringWithWrongPlayerType() {
        //given
        String value = TOKEN + "playerType";

        //when
        playlistData = new PlaylistData(value);

        //then
        assertNotNull(playlistData);
    }

    @Test
    public void shouldConstructFromStringWithOneTokenAndPlayerType() {
        //given
        String value = TOKEN + PlayerType.MINI_PLAYER_ONLY;

        //when
        playlistData = new PlaylistData(value);

        //then
        assertNotNull(playlistData);
    }

    @Test
    public void shouldGetChartIdString() {
        //given
        Integer chartId = Integer.MAX_VALUE;
        playlistData = new PlaylistData(chartId, MINI_PLAYER_ONLY);

        //when
        String actualChartId = playlistData.getChartIdString();

        //then
        assertThat(actualChartId, is(String.valueOf(chartId)));
    }

    @Test
    public void shouldGetChartIdStringAsNull() {
        //given
        Integer chartId = null;
        playlistData = new PlaylistData(chartId, MINI_PLAYER_ONLY);

        //when
        String actualChartId = playlistData.getChartIdString();

        //then
        assertNull(actualChartId);
    }

    @Test
    public void shouldGetChartId() {
        //given
        Integer chartId = Integer.MAX_VALUE;
        playlistData = new PlaylistData(chartId, MINI_PLAYER_ONLY);

        //when
        Integer actualChartId = playlistData.getChartId();

        //then
        assertThat(actualChartId, is(chartId));
    }

    @Test
    public void shouldGetChartIdAsNull() {
        //given
        Integer chartId = null;
        playlistData = new PlaylistData(chartId, MINI_PLAYER_ONLY);

        //when
        Integer actualChartId = playlistData.getChartId();

        //then
        assertNull(actualChartId);
    }

    @Test
    public void shouldGetPlayerType() {
        //given
        Integer chartId = Integer.MAX_VALUE;
        PlayerType playerType = MINI_PLAYER_ONLY;
        playlistData = new PlaylistData(chartId, playerType);

        //when
        PlayerType actualPlayerType = playlistData.getPlayerType();

        //then
        assertThat(actualPlayerType, is(playerType));
    }

    @Test
    public void shouldGetPlayerTypeString() {
        //given
        Integer chartId = Integer.MAX_VALUE;
        PlayerType playerType = MINI_PLAYER_ONLY;
        playlistData = new PlaylistData(chartId, playerType);

        //when
        String actualPlayerType = playlistData.getPlayerTypeString();

        //then
        assertThat(actualPlayerType, is(playerType.name()));
    }

    @Test
    public void shouldToValueString() {
        //given
        Integer chartId = Integer.MAX_VALUE;
        PlayerType playerType = MINI_PLAYER_ONLY;
        playlistData = new PlaylistData(chartId, playerType);

        //when
        String valueString = playlistData.toValueString();

        //then
        assertThat(valueString, is(Joiner.on(TOKEN).join(chartId, playerType)));
    }

    @Test
    public void shouldToValueStringWhenChartIdIsNull() {
        //given
        Integer chartId = null;
        PlayerType playerType = MINI_PLAYER_ONLY;
        playlistData = new PlaylistData(chartId, playerType);

        //when
        String valueString = playlistData.toValueString();

        //then
        assertThat(valueString, is(TOKEN + playerType));
    }
}