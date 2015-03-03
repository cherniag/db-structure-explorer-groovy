package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import static mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData.TOKEN;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType.MINI_PLAYER_ONLY;

import com.google.common.base.Joiner;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TrackDataTest {

    TrackData trackData;

    @Test(expected = NullPointerException.class)
    public void shouldNotConstruct() {
        trackData = new TrackData(new Media(), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotConstructFromNullString() {
        trackData = new TrackData(null);
    }

    public void shouldConstructFromStringWithWrongPlayerType() {
        //given
        String value = DeepLinkInfoService.PlaylistData.TOKEN + "playerType";

        //when
        trackData = new TrackData(value);

        //then
        assertNotNull(trackData);
    }

    @Test
    public void shouldConstructFromStringWithOneTokenAndPlayerType() {
        //given
        String value = TOKEN + PlayerType.MINI_PLAYER_ONLY;

        //when
        trackData = new TrackData(value);

        //then
        assertNotNull(trackData);
    }

    @Test
    public void shouldGetMediaIdString() {
        //given
        Media media = new Media();
        media.setI(Integer.MAX_VALUE);
        trackData = new TrackData(media, MINI_PLAYER_ONLY);

        //when
        String actualMediaIdString = trackData.getMediaIdString();

        //then
        assertThat(actualMediaIdString, is(String.valueOf(media.getI())));
    }

    @Test
    public void shouldGetMediaIdStringAsNull() {
        //given
        Media media = new Media();
        trackData = new TrackData(media, MINI_PLAYER_ONLY);

        //when
        String actualMediaIdString = trackData.getMediaIdString();

        //then
        assertThat(actualMediaIdString, is("null"));
    }

    @Test
    public void shouldGetMediaId() {
        //given
        Media media = new Media();
        media.setI(Integer.MAX_VALUE);
        trackData = new TrackData(media, MINI_PLAYER_ONLY);

        //when
        Integer actualMediaId = trackData.getMediaId();

        //then
        assertThat(actualMediaId, is(media.getI()));
    }

    @Test
    public void shouldGetPlayerType() {
        //given
        Media media = new Media();
        PlayerType playerType = MINI_PLAYER_ONLY;
        trackData = new TrackData(media, playerType);

        //when
        PlayerType actualPlayerType = trackData.getPlayerType();

        //then
        assertThat(actualPlayerType, is(playerType));
    }

    @Test
    public void shouldGetPlayerTypeString() {
        //given
        Media media = new Media();
        PlayerType playerType = MINI_PLAYER_ONLY;
        trackData = new TrackData(media, playerType);

        //when
        String actualPlayerType = trackData.getPlayerTypeString();

        //then
        assertThat(actualPlayerType, is(playerType.name()));
    }

    @Test
    public void shouldToValueString() {
        //given
        Media media = new Media();
        media.setI(Integer.MAX_VALUE);
        PlayerType playerType = MINI_PLAYER_ONLY;
        trackData = new TrackData(media, playerType);

        //when
        String valueString = trackData.toValueString();

        //then
        assertThat(valueString, is(Joiner.on(TOKEN).join(media.getI(), playerType)));
    }

    @Test
    public void shouldToValueStringWhenChartIdIsNull() {
        //given
        Media media = new Media();
        media.setI(null);
        PlayerType playerType = MINI_PLAYER_ONLY;
        trackData = new TrackData(media, playerType);

        //when
        String valueString = trackData.toValueString();

        //then
        assertThat(valueString, is("null" + TOKEN + playerType));
    }
}