package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.domain.streamzine.RecognizedAction;
import mobi.nowtechnologies.server.dto.streamzine.MusicType;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.GrantedToType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/24/14
 */
@RunWith(MockitoJUnitRunner.class)
public class DeepLinkInfoServiceTest {
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    private DeepLinkInfoService deepLinkInfoService;

    @Test
    public void testTryToHandleSecuredTileForSecuredAction() throws Exception {
        OrdinalBlockDto deepLinkInfo = mock(OrdinalBlockDto.class);
        when(deepLinkInfo.getKey()).thenReturn(LinkLocationType.INTERNAL_AD.name());
        when(deepLinkInfo.getContentType()).thenReturn(ContentType.PROMOTIONAL);
        when(deepLinkInfo.getValue()).thenReturn("page#" + RecognizedAction.SUBSCRIBE.getId());

        AccessPolicy accessPolicy = deepLinkInfoService.tryToHandleSecuredTile(deepLinkInfo);

        assertEquals(Permission.HIDDEN, accessPolicy.getPermission());
        assertEquals(1, accessPolicy.getUserStatusTypes().size());
        assertTrue(accessPolicy.getUserStatusTypes().contains(GrantedToType.SUBSCRIBED));
    }

    @Test
    public void testTryToHandleSecuredTileForVip() throws Exception {
        OrdinalBlockDto deepLinkInfo = mock(OrdinalBlockDto.class);
        when(deepLinkInfo.isVip()).thenReturn(true);
        when(deepLinkInfo.getKey()).thenReturn(MusicType.PLAYLIST.name());
        when(deepLinkInfo.getContentType()).thenReturn(ContentType.MUSIC);
        when(deepLinkInfo.getValue()).thenReturn(ChartType.BASIC_CHART.name());

        AccessPolicy accessPolicy = deepLinkInfoService.tryToHandleSecuredTile(deepLinkInfo);

        assertEquals(Permission.RESTRICTED, accessPolicy.getPermission());
        assertEquals(2, accessPolicy.getUserStatusTypes().size());
        assertTrue(accessPolicy.getUserStatusTypes().contains(GrantedToType.LIMITED));
        assertTrue(accessPolicy.getUserStatusTypes().contains(GrantedToType.FREETRIAL));
    }

    @Test
    public void createManualCompilationDLInfoWithIsrcDuplicates() throws Exception {
        when(mediaRepository.getByIsrc(eq("ISRC10"))).thenReturn(getMedia(10, "ISRC10"));
        when(mediaRepository.getByIsrc(eq("ISRC11"))).thenReturn(getMedia(11, "ISRC11"));
        when(mediaRepository.getByIsrc(eq("ISRC12"))).thenReturn(getMedia(12, "ISRC12"));

        OrdinalBlockDto blockDto = createOrdinalBlockDto(ContentType.MUSIC, MusicType.MANUAL_COMPILATION.name(), "ISRC10#ISRC12#ISRC11#ISRC12");

        DeeplinkInfo deeplinkInfo = deepLinkInfoService.create(blockDto);
        assertThat(deeplinkInfo, notNullValue());
        assertThat(deeplinkInfo, instanceOf(ManualCompilationDeeplinkInfo.class));
        ManualCompilationDeeplinkInfo i = (ManualCompilationDeeplinkInfo) deeplinkInfo;
        assertThat(i.getMediaIds(), hasSize(3));
        List<Media> medias = i.getMedias();
        assertThat(medias, hasSize(3));
        assertThat(medias.get(0).getI(), is(10));
        assertThat(medias.get(1).getI(), is(12));
        assertThat(medias.get(2).getI(), is(11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createManualCompilationDLInfoWithNotExistingIsrc() throws Exception {
        when(mediaRepository.getByIsrc(eq("ISRC11"))).thenReturn(getMedia(11, "ISRC11"));
        when(mediaRepository.getByIsrc(eq("ISRC12"))).thenReturn(getMedia(12, "ISRC12"));

        OrdinalBlockDto blockDto = createOrdinalBlockDto(ContentType.MUSIC, MusicType.MANUAL_COMPILATION.name(), "ISRC10#ISRC11#ISRC12");

        deepLinkInfoService.create(blockDto);
    }

    private OrdinalBlockDto createOrdinalBlockDto(ContentType contentType, String key, String value) {
        OrdinalBlockDto blockDto = new OrdinalBlockDto();
        blockDto.setContentType(contentType);
        blockDto.setKey(key);
        blockDto.setValue(value);
        return blockDto;
    }

    private Media getMedia(int i, String isrc) {
        Media media1 = new Media();
        media1.setI(i);
        media1.setIsrc(isrc);
        return media1;
    }
}
