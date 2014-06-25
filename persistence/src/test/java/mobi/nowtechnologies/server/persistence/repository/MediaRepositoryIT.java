package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.*;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;

import static java.util.Collections.singletonList;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.VIDEO;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/*
 * @author Titov Mykhaylo (titov)
 */
public class MediaRepositoryIT extends AbstractRepositoryIT {

    @Resource(name="mediaRepository")
    MediaRepository mediaRepository;

    @Resource(name="artistRepository")
    ArtistRepository artistRepository;

    @Resource(name="mediaFileRepository")
    MediaFileRepository mediaFileRepository;

    @Test
    public void shouldReturnMediaWhenWeSearchAudioAndArtistNameEqSearchWords() throws Exception {
        //given
        String searchWords = "artist name";
        byte mediaType = MOBILE_AUDIO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

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

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

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

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

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

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

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

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

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

        Media media = mediaRepository.save(new Media().withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchAudioAndMediaTrackIdEqSearchWords() throws Exception {
        //given
        String searchWords = "666";
        byte mediaType = MOBILE_AUDIO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));

        Media media = mediaRepository.save(new Media().withTrackId(666L).withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }

    @Test
    public void shouldReturnMediaWhenWeSearchVideoAndMediaTrackIdEqSearchWords() throws Exception {
        //given
        String searchWords = "666";
        byte mediaType = VIDEO.getIdAsByte();

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(mediaType).withName(VIDEO.name())));
        MediaFile imageFileSmall = mediaFileRepository.save(new MediaFile().withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));

        Media media = mediaRepository.save(new Media().withTrackId(666L).withIsrc("media isrc").withTitle("media title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmall));

        //when
        List<Media> medias = mediaRepository.getMedias(searchWords, mediaType);

        //then
        assertThat(medias.size(), is(1));
        assertThat(medias.get(0).getI(), is(media.getI()));
    }
}