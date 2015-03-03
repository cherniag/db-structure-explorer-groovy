package mobi.nowtechnologies.server.admin.settings.asm;

import mobi.nowtechnologies.server.admin.settings.asm.dto.SettingsDto;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.MetaInfo;
import mobi.nowtechnologies.server.admin.settings.service.SettingsService;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.service.behavior.BehaviorInfoService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SettingsAsmTest {

    @InjectMocks
    SettingsAsm settingsAsm;
    @Mock
    MessageSource messageSource;
    @Mock
    SettingsService settingsService;
    @Mock
    BehaviorInfoService behaviorInfoService;

    @Captor
    ArgumentCaptor<Map<ChartBehaviorType, MetaInfo>> metaInfoArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDto() throws Exception {
        // given
        String url = "url";
        List<ChartListItemDto> chartList = mock(List.class);
        Set<String> pages = mock(Set.class);
        Set<String> actions = mock(Set.class);


        SettingsDto dto = mock(SettingsDto.class);
        when(settingsService.export(url)).thenReturn(dto);

        // when
        SettingsDto enriched = settingsAsm.createDto(url, chartList, pages, actions);

        // then
        verify(dto).setActions(actions);
        verify(dto).setPages(pages);
        verify(dto).addPlaylistInfo(chartList);
        verify(dto).setMetaInfo(metaInfoArgumentCaptor.capture());
        assertSame(enriched, dto);

        assertTrue(metaInfoArgumentCaptor.getValue().get(ChartBehaviorType.SHUFFLED).isTracksInfoSupported());
        assertTrue(metaInfoArgumentCaptor.getValue().get(ChartBehaviorType.PREVIEW).isTracksPlayDurationSupported());
    }
}