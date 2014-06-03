package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.*;

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
}