package mobi.nowtechnologies.server.admin.validator;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UpdateValidator.class, WebUtils.class, RequestContextHolder.class})
public class UpdateValidatorTest {
    @Mock
    MessageSource messageSource;

    @Mock
    MessageRepository messageRepository;

    @Mock
    MediaService mediaService;

    @Mock
    MobileApplicationPagesService mobileApplicationPagesService;

    @Mock
    StreamzineTypesMappingService streamzineTypesMappingService;

    @InjectMocks
    UpdateValidator updateValidator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidateBadgeWhenAllowed() throws Exception {
        OrdinalBlockDto blockDto = mock(OrdinalBlockDto.class);
        Errors errors = mock(Errors.class);

        // allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);

        updateValidator.validateBadge(blockDto, errors);

        verifyNoMoreInteractions(errors);
    }

    @Test
    public void testValidateBadgeWhenNotAllowedAndBadgeUrlNotProvided() throws Exception {
        OrdinalBlockDto blockDto = mock(OrdinalBlockDto.class);
        Errors errors = mock(Errors.class);

        // allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);
        // not assigned
        when(blockDto.getBadgeUrl()).thenReturn(null);

        updateValidator.validateBadge(blockDto, errors);

        verifyNoMoreInteractions(errors);
    }

    @Test
    public void testValidateBadgeWhenNotAllowedAndBadgeUrlProvided() throws Exception {
        OrdinalBlockDto blockDto = mock(OrdinalBlockDto.class);
        Errors errors = mock(Errors.class);

        // not allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.SLIM_BANNER);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);
        // not assigned
        when(blockDto.getBadgeUrl()).thenReturn("not-empty-badge-url");

        updateValidator.validateBadge(blockDto, errors);

        verify(errors).rejectValue("badgeUrl", "streamzine.error.badge.notallowed", null);
    }

    @Test
    public void shouldAddBlocksTrackContentDuplicationErrorWhenTracksAreTheSame(){
        //given

        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDtoMock.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideKeyString()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        ServletRequestAttributes servletRequestAttributesMock = new ServletRequestAttributes(new MockHttpServletRequest());

        mockStatic(WebUtils.class);
        when(WebUtils.getCookie(any(HttpServletRequest.class), any(String.class))).thenReturn(new Cookie("", ""));

        mockStatic(RequestContextHolder.class);
        when(RequestContextHolder.getRequestAttributes()).thenReturn(servletRequestAttributesMock);
        HashSet<Media> medias = new HashSet<Media>();
        medias.add(new Media());

        when(mediaService.getMediasByChartAndPublishTimeAndMediaIsrcs(any(String.class), anyLong(), anyList())).thenReturn(medias);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(1)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldAddBlocksPlaylistContentDuplicationErrorWhenPlaylistsAreTheSame(){
        //given

        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDtoMock.getKey()).thenReturn(MusicType.PLAYLIST.name());
        when(blockDtoMock.provideKeyString()).thenReturn(MusicType.PLAYLIST.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(1)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldAddBlocksPlaylistContentDuplicationErrorWhenNewsStoriesAreTheSame(){
        //given

        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.NARROW);
        when(blockDtoMock.getKey()).thenReturn(NewsType.STORY.name());
        when(blockDtoMock.provideKeyString()).thenReturn(NewsType.STORY.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.NEWS);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(1)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldAddBlocksPlaylistContentDuplicationErrorWhenNewsListsAreTheSame(){
        //given

        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);
        OrdinalBlockDto blockDtoMock2 = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.NARROW);
        when(blockDtoMock.getKey()).thenReturn(NewsType.STORY.name());
        when(blockDtoMock.provideKeyString()).thenReturn(NewsType.STORY.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.NEWS);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        when(blockDtoMock2.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDtoMock2.getKey()).thenReturn(NewsType.LIST.name());
        when(blockDtoMock2.provideKeyString()).thenReturn(NewsType.LIST.name());
        when(blockDtoMock2.provideValueString()).thenReturn("Stairway to heaven");
        when(blockDtoMock2.getContentType()).thenReturn(ContentType.NEWS);
        when(blockDtoMock2.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock2);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock2);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(3)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldNotAddBlocksDuplicationErrorWhenValuesAreTheSameButKeysAreDiffers(){
        //given

        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDtoMock.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideKeyString()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        OrdinalBlockDto blockDtoMock2 = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock2.getShapeType()).thenReturn(ShapeType.NARROW);
        when(blockDtoMock2.getKey()).thenReturn(MusicType.PLAYLIST.name());
        when(blockDtoMock2.provideKeyString()).thenReturn(MusicType.PLAYLIST.name());
        when(blockDtoMock2.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock2.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock2.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock2);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        ServletRequestAttributes servletRequestAttributesMock = new ServletRequestAttributes(new MockHttpServletRequest());

        mockStatic(WebUtils.class);
        when(WebUtils.getCookie(any(HttpServletRequest.class), any(String.class))).thenReturn(new Cookie("", ""));

        mockStatic(RequestContextHolder.class);
        when(RequestContextHolder.getRequestAttributes()).thenReturn(servletRequestAttributesMock);
        HashSet<Media> medias = new HashSet<Media>();
        medias.add(new Media());

        when(mediaService.getMediasByChartAndPublishTimeAndMediaIsrcs(any(String.class), anyLong(), anyList())).thenReturn(medias);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(0)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldNotAddBlocksDuplicationErrorWhenTracksAreDiffer(){
        //given
        Errors errors = mock(Errors.class);
        OrdinalBlockDto blockDtoMock = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDtoMock.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideKeyString()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock.provideValueString()).thenReturn("Highway to hell");
        when(blockDtoMock.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock.isIncluded()).thenReturn(true);

        OrdinalBlockDto blockDtoMock2 = mock(OrdinalBlockDto.class);

        // allowed
        when(blockDtoMock2.getShapeType()).thenReturn(ShapeType.NARROW);
        when(blockDtoMock2.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock2.provideKeyString()).thenReturn(MusicType.TRACK.name());
        when(blockDtoMock2.provideValueString()).thenReturn("Soul Kitchen");
        when(blockDtoMock2.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDtoMock2.isIncluded()).thenReturn(true);

        UpdateIncomingDto updateIncomingDtoMock = new UpdateIncomingDto();
        updateIncomingDtoMock.getBlocks().add(blockDtoMock);
        updateIncomingDtoMock.getBlocks().add(blockDtoMock2);

        TypesMappingInfo typesMappingInfoMock = mock(TypesMappingInfo.class);

        ServletRequestAttributes servletRequestAttributesMock = new ServletRequestAttributes(new MockHttpServletRequest());

        mockStatic(WebUtils.class);
        when(WebUtils.getCookie(any(HttpServletRequest.class), any(String.class))).thenReturn(new Cookie("", ""));

        mockStatic(RequestContextHolder.class);
        when(RequestContextHolder.getRequestAttributes()).thenReturn(servletRequestAttributesMock);
        HashSet<Media> medias = new HashSet<Media>();
        medias.add(new Media());

        when(mediaService.getMediasByChartAndPublishTimeAndMediaIsrcs(any(String.class), anyLong(), anyList())).thenReturn(medias);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);

        //when
        updateValidator.customValidate(updateIncomingDtoMock, errors);

        //then
        verify(errors, times(0)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }
}