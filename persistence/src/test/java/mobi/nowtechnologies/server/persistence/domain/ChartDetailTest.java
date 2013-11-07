package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.junit.Test;

import java.util.Collections;

import static java.util.Collections.*;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: Titov Mykhaylo (titov)
 * 04.11.13 15:11
 */
public class ChartDetailTest {

    private ChartDetail chartDetail;
    private ChartDetailDto chartDetailDto;

    @Test
    public void shouldConvertToChartDetailDto() throws Exception {
        //given
        chartDetail = new ChartDetail().withChgPosition(UNCHANGED).withPrevPosition(Byte.MAX_VALUE).withChart(new Chart().withI(Integer.MAX_VALUE).withGenre(new Genre())).withMedia(new Media().withITunesUrl("http://clkuk.tradedoubler.com/click?p=23708&a&1997010&url=http://7digital.com/NZ/album/sugar/id212144821?i&212144846&uo=4&partnerId&2003").withGenre(new Genre()).withImageFileLarge(new MediaFile()).withImageFileSmall(new MediaFile()).withAudioFile(new MediaFile()).withArtist(new Artist()).withDrms(singletonList(new Drm().withDrmType(new DrmType()))));
        Community community = new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL);

        //when
        chartDetailDto = chartDetail.toChartDetailDto(community, new ChartDetailDto(), "http://clkuk.tradedoubler.com/click?p=23708&a&1997010&url=http://itunes.apple.com/NZ/album/sugar/id212144821?i&212144846&uo=4&partnerId&2003");

        //then
        shouldConvertToChartDetailDtoSuccessfully();
    }

    private void shouldConvertToChartDetailDtoSuccessfully() {
        assertThat(chartDetailDto, is(notNullValue()));
        assertThat(chartDetailDto.getPosition(), is(chartDetail.getPosition()));
        assertThat(chartDetailDto.getPlaylistId(), is(chartDetail.getChart().getI()));
        Media media = chartDetail.getMedia();
        assertThat(chartDetailDto.getArtist(), is(media.getArtistName()));
        assertThat(chartDetailDto.getAudioSize(), is(media.getAudioSize()));
        Drm drm = media.getDrms().get(0);
        assertThat(chartDetailDto.getDrmType(), is(drm.getDrmType().getName()));
        assertThat(chartDetailDto.getDrmValue(), is(drm.getDrmValue()));
        assertThat(chartDetailDto.getGenre1(), is(chartDetail.getChart().getGenre().getName()));
        assertThat(chartDetailDto.getGenre2(), is(media.getGenre().getName()));

        int audioSize = media.getAudioSize();
        int headerSize = media.getHeaderSize();

        assertThat(chartDetailDto.getHeaderSize(), is(headerSize));
        assertThat(chartDetailDto.getImageLargeSize(), is(media.getImageLargeSize()));
        assertThat(chartDetailDto.getImageSmallSize(), is(media.getImageSmallSize()));
        assertThat(chartDetailDto.getInfo(), is(chartDetail.getInfo()));
        assertThat(chartDetailDto.getMedia(), is(media.getIsrc()));
        assertThat(chartDetailDto.getTitle(), is(media.getTitle()));
        assertThat(chartDetailDto.getTrackSize(), is(headerSize + audioSize - 2));
        assertThat(chartDetailDto.getChartDetailVersion(), is(chartDetail.getVersion()));
        assertThat(chartDetailDto.getHeaderVersion(), is(0));
        assertThat(chartDetailDto.getAudioVersion(), is(media.getAudioFile().getVersion()));
        assertThat(chartDetailDto.getImageLargeVersion(), is(media.getImageFIleLarge().getVersion()));
        assertThat(chartDetailDto.getImageSmallVersion(), is(media.getImageFileSmall().getVersion()));
        assertThat(chartDetailDto.getDuration(), is(media.getAudioFile().getDuration()));
        assertThat(chartDetailDto.getAmazonUrl(), is("http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%26a%261997010%26212144846%26uo%3D4%26partnerId%262003%26url%3Dhttp%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fsugar%2Fid212144821%3Fi"));
        assertThat(chartDetailDto.getiTunesUrl(), is("http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%26a%261997010%26212144846%26uo%3D4%26partnerId%262003%26url%3Dhttp%3A%2F%2F7digital.com%2FGB%2Falbum%2Fsugar%2Fid212144821%3Fi"));
        assertThat(chartDetailDto.isArtistUrl(), is(media.getAreArtistUrls()));
        assertThat(chartDetailDto.getPreviousPosition(), is(chartDetail.getPrevPosition()));
        assertThat(chartDetailDto.getChangePosition(), is(chartDetail.getChgPosition().getLabel()));
        assertThat(chartDetailDto.getChannel(), is(chartDetail.getChannel()));
    }
}
