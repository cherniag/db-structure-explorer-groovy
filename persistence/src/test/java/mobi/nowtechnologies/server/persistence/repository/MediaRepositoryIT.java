package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.VIDEO;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.is;

/*
 * @author Titov Mykhaylo (titov)
 */
public class MediaRepositoryIT extends AbstractRepositoryIT {

    @Resource(name = "mediaRepository")
    MediaRepository mediaRepository;
    @Resource(name = "artistRepository")
    ArtistRepository artistRepository;
    @Resource(name = "mediaFileRepository")
    MediaFileRepository mediaFileRepository;
    @Resource(name = "labelRepository")
    LabelRepository labelRepository;

    @Test
    public void shouldReturnMediaWhenWeSearchAudioAndArtistNameEqSearchWords() throws Exception {
        //given
        String searchWords = "artist name";
        byte mediaType = MOBILE_AUDIO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchVideoAndArtistNameEqSearchWords() throws Exception {
        //given
        String searchWords = "artist name";
        byte mediaType = VIDEO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(VIDEO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchAudioAndMediaIsrcEqSearchWords() throws Exception {
        //given
        String searchWords = "media isrc";
        byte mediaType = MOBILE_AUDIO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchVideoAndMediaIsrcEqSearchWords() throws Exception {
        //given
        String searchWords = "media isrc";
        byte mediaType = VIDEO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(VIDEO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchAudioAndMediaTitleEqSearchWords() throws Exception {
        //given
        String searchWords = "media title";
        byte mediaType = MOBILE_AUDIO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchVideoAndMediaTitleEqSearchWords() throws Exception {
        //given
        String searchWords = "media title";
        byte mediaType = VIDEO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(VIDEO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(9L);

        Media media = mediaRepository
            .save(new Media().withIsrc("media isrc").withTitle("media title").withLabel(label).withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall).withTrackId(1L));

        //when
        List<Media> medias = mediaRepository.findMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }
}