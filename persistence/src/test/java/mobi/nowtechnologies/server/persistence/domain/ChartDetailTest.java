package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.junit.Test;

import java.util.Collections;

import static java.util.Collections.*;
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
        chartDetail = new ChartDetail().withChgPosition(UNCHANGED).withPrevPosition(Byte.MAX_VALUE).withChart(new Chart().withI(Integer.MAX_VALUE).withGenre(new Genre())).withMedia(new Media().withITunesUrl("https://itunes/o2/file").withGenre(new Genre()).withImageFileLarge(new MediaFile()).withImageFileSmall(new MediaFile()).withAudioFile(new MediaFile()).withArtist(new Artist()).withDrms(singletonList(new Drm().withDrmType(new DrmType()))));
        Community community = new Community().withRewriteUrl("Kyiv");

        //when
        chartDetailDto = chartDetail.toChartDetailDto(community, new ChartDetailDto(), "https://amazon//file");

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
        assertThat(chartDetailDto.getAmazonUrl(), is("https%3A%2F%2Famazon%2FKyiv%2Ffile"));
        assertThat(chartDetailDto.getiTunesUrl(), is("https%3A%2F%2Fitunes%2FKyiv%2Ffile"));
        assertThat(chartDetailDto.isArtistUrl(), is(media.getAreArtistUrls()));
        assertThat(chartDetailDto.getPreviousPosition(), is(chartDetail.getPrevPosition()));
        assertThat(chartDetailDto.getChangePosition(), is(chartDetail.getChgPosition().getLabel()));
        assertThat(chartDetailDto.getChannel(), is(chartDetail.getChannel()));
    }
}
