package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.RecognizedAction;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.domain.user.GrantedToType;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;

import java.util.List;

import com.google.common.base.Joiner;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Author: Gennadii Cherniaiev Date: 3/24/14
 */
@RunWith(MockitoJUnitRunner.class)
public class DeepLinkInfoServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
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
        assertEquals(1, accessPolicy.getGrantedToTypes().size());
        assertTrue(accessPolicy.getGrantedToTypes().contains(GrantedToType.SUBSCRIBED));
    }

    @Test
    public void testTryToHandleSecuredTileForVip() throws Exception {
        OrdinalBlockDto deepLinkInfo = mock(OrdinalBlockDto.class);
        when(deepLinkInfo.isVip()).thenReturn(true);
        when(deepLinkInfo.getKey()).thenReturn(MusicType.PLAYLIST.name());
        when(deepLinkInfo.getContentType()).thenReturn(ContentType.MUSIC);
        when(deepLinkInfo.getValue()).thenReturn("666#" + PlayerType.REGULAR_PLAYER_ONLY);

        AccessPolicy accessPolicy = deepLinkInfoService.tryToHandleSecuredTile(deepLinkInfo);

        assertEquals(Permission.RESTRICTED, accessPolicy.getPermission());
        assertEquals(2, accessPolicy.getGrantedToTypes().size());
        assertTrue(accessPolicy.getGrantedToTypes().contains(GrantedToType.LIMITED));
        assertTrue(accessPolicy.getGrantedToTypes().contains(GrantedToType.FREETRIAL));
    }

    @Test
    public void createManualCompilationDLInfoWithIdsDuplicates() throws Exception {
        final int firstMediaId = 10;
        final int secondMediaId = 11;
        final int thirdMediaId = 12;

        when(mediaRepository.findOne(firstMediaId)).thenReturn(getMedia(firstMediaId));
        when(mediaRepository.findOne(secondMediaId)).thenReturn(getMedia(secondMediaId));
        when(mediaRepository.findOne(thirdMediaId)).thenReturn(getMedia(thirdMediaId));

        OrdinalBlockDto blockDto = createOrdinalBlockDto(ContentType.MUSIC, MusicType.MANUAL_COMPILATION.name(), composeIds(firstMediaId, secondMediaId, thirdMediaId, firstMediaId));

        DeeplinkInfo deeplinkInfo = deepLinkInfoService.create(blockDto);
        assertNotNull(deeplinkInfo);
        assertTrue(deeplinkInfo instanceof ManualCompilationDeeplinkInfo);
        ManualCompilationDeeplinkInfo i = (ManualCompilationDeeplinkInfo) deeplinkInfo;
        assertEquals(3, i.getMediaIds().size());

        List<Media> medias = i.getMedias();
        assertEquals(3, medias.size());
        assertEquals(firstMediaId, medias.get(0).getI().intValue());
        assertEquals(secondMediaId, medias.get(1).getI().intValue());
        assertEquals(thirdMediaId, medias.get(2).getI().intValue());
    }

    @Test
    public void createManualCompilationDLInfoWithNotExistingIds() throws Exception {
        final int nonExistingMediaId = 10;
        final int secondMediaId = 11;
        final int thirdMediaId = 12;

        when(mediaRepository.findOne(secondMediaId)).thenReturn(getMedia(secondMediaId));
        when(mediaRepository.findOne(thirdMediaId)).thenReturn(getMedia(thirdMediaId));

        OrdinalBlockDto blockDto = createOrdinalBlockDto(ContentType.MUSIC, MusicType.MANUAL_COMPILATION.name(), composeIds(nonExistingMediaId, secondMediaId, thirdMediaId));

        thrown.expect(IllegalArgumentException.class);

        deepLinkInfoService.create(blockDto);
    }

    private String composeIds(Integer... ids) {
        return Joiner.on('#').join(ids);
    }

    private OrdinalBlockDto createOrdinalBlockDto(ContentType contentType, String key, String value) {
        OrdinalBlockDto blockDto = new OrdinalBlockDto();
        blockDto.setContentType(contentType);
        blockDto.setKey(key);
        blockDto.setValue(value);
        return blockDto;
    }

    private Media getMedia(int i) {
        Media media1 = new Media();
        media1.setI(i);
        return media1;
    }
}
