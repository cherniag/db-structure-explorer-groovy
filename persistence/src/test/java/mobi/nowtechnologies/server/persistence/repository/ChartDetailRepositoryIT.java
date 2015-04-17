package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import static mobi.nowtechnologies.server.shared.enums.ChartType.BASIC_CHART;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;

import javax.annotation.Resource;

import java.util.List;
import static java.util.Arrays.asList;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

// @author Titov Mykhaylo (titov)
public class ChartDetailRepositoryIT extends AbstractRepositoryIT {

    @Resource
    ChartDetailRepository chartDetailRepository;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    ChartRepository chartRepository;
    @Resource
    GenreRepository genreRepository;
    @Resource
    MediaRepository mediaRepository;
    @Resource
    ArtistRepository artistRepository;
    @Resource
    MediaFileRepository mediaFileRepository;
    @Resource
    LabelRepository labelRepository;

    @Test
    public void shouldFindNearestFeatureChartPublishDate() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L));
        ChartDetail secondUpdate = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L));

        long chosenPublishTimeMillis = 2L;
        Integer chartId = chart.getI();

        //when
        Long nearestFeatureChartPublishDate = chartDetailRepository.findNearestFeatureChartPublishDate(chosenPublishTimeMillis, chartId);

        //then
        assertThat(nearestFeatureChartPublishDate, is(secondUpdate.getPublishTimeMillis()));
    }

    @Test
    public void shouldReturnNullWhenNoFeatureUpdate() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L));

        long chosenPublishTimeMillis = 7L;
        Integer chartId = chart.getI();

        //when
        Long nearestFeatureChartPublishDate = chartDetailRepository.findNearestFeatureChartPublishDate(chosenPublishTimeMillis, chartId);

        //then
        assertThat(nearestFeatureChartPublishDate, is(nullValue()));
    }

    @Test
    public void shouldFindNearestFeatureChartPublishDateBeforeGivenDate() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));
        long publishTimeMillis = 1;
        long beforeDateTimeMillis = 6;

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L));
        ChartDetail secondUpdate = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L));

        Integer chartId = chart.getI();

        //when
        Long nearestFeatureChartPublishDate = chartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(publishTimeMillis, beforeDateTimeMillis, chartId);

        //then
        assertThat(nearestFeatureChartPublishDate, is(secondUpdate.getPublishTimeMillis()));
    }

    @Test
    public void shouldFindNearestFeatureChartPublishDateBeforeGivenDateOfFirstUpdate() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));
        long publishTimeMillis = 0;
        long beforeDateTimeMillis = 6L;

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        ChartDetail firstUpdate = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L));

        Integer chartId = chart.getI();

        //when
        Long nearestFeatureChartPublishDate = chartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(publishTimeMillis, beforeDateTimeMillis, chartId);

        //then
        assertThat(nearestFeatureChartPublishDate, is(firstUpdate.getPublishTimeMillis()));
    }

    @Test
    public void shouldNotFindNearestFeatureChartPublishDateBeforeGivenDateWhenNoUpdatesOnGivenTimePeriod() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));
        long publishTimeMillis = 2;
        long beforeDateTimeMillis = 6;

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L));

        Integer chartId = chart.getI();

        //when
        Long nearestFeatureChartPublishDate = chartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(publishTimeMillis, beforeDateTimeMillis, chartId);

        //then
        assertThat(nearestFeatureChartPublishDate, is(nullValue()));
    }

    @Test
    public void shouldGetDuplicatedMediaChartDetails() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media1 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media2 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media3 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        ChartDetail chartDetail1 = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L).withMedia(media1));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media2));
        ChartDetail chartDetail3 = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media3));
        ChartDetail chartDetail4 = chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L).withMedia(media3));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartDetailRepository.findDuplicatedMediaChartDetails(chart, asList(1L, 5L, 6L), asList(media1.getI(), media3.getI()));

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(3));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(chartDetail1.getI()));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(chartDetail3.getI()));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(chartDetail4.getI()));
    }

    @Test
    public void shouldNotGetDuplicatedMediaChartDetailsWhenNoUpdatesOnSuchPublishTimes() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media1 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media2 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media3 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L).withMedia(media1));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media2));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media3));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L).withMedia(media3));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartDetailRepository.findDuplicatedMediaChartDetails(chart, asList(-1L, -5L, -6L), asList(media1.getI(), media3.getI()));

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));
    }

    @Test
    public void shouldNotGetDuplicatedMediaChartDetailsWhenNoDuplicates() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media1 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media2 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media3 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L).withMedia(media1));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media2));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media3));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L).withMedia(media3));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartDetailRepository.findDuplicatedMediaChartDetails(chart, asList(1L, 5L, 6L), asList(-1, -2));

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));
    }

    @Test
    public void shouldNotGetDuplicatedMediaChartDetailsWhenNoChart() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media1 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media2 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));
        Media media3 = mediaRepository.save(
            new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Chart chart = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(1L).withMedia(media1));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media2));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(5L).withMedia(media3));
        chartDetailRepository.save(new ChartDetail().withChart(chart).withPosition(1).withPublishTime(6L).withMedia(media3));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartDetailRepository.findDuplicatedMediaChartDetails(null, asList(1L, 5L, 6L), asList(media1.getI(), media3.getI()));

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));
    }
}