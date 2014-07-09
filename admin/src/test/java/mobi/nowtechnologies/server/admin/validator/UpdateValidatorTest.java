package mobi.nowtechnologies.server.admin.validator;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

@RunWith(PowerMockRunner.class)
public class UpdateValidatorTest {
    private static final String HL_UK = "hl_uk";

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
    @Mock
    UserRepository userRepository;
    @Mock
    CookieUtil cookieUtil;
    
    @InjectMocks
    UpdateValidator updateValidator ;

    @Mock
    OrdinalBlockDto blockDto;
    @Mock
    Errors errors;
    @Mock
    TypesMappingInfo typesMappingInfoMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(streamzineTypesMappingService.getTypesMappingInfos()).thenReturn(typesMappingInfoMock);
        when(typesMappingInfoMock.matches(any(DeeplinkInfoData.class))).thenReturn(true);
        when(cookieUtil.get(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME)).thenReturn(HL_UK);
    }

    @Test
    public void testValidateBadgeWhenAllowed() throws Exception {
        // allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);

        updateValidator.validateBadgeMapping(blockDto, errors);

        verifyNoMoreInteractions(errors);
    }

    @Test
    public void testValidateBadgeWhenNotAllowedAndBadgeUrlNotProvided() throws Exception {
        // allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);
        // not assigned
        when(blockDto.getBadgeUrl()).thenReturn(null);

        updateValidator.validateBadgeMapping(blockDto, errors);

        verifyNoMoreInteractions(errors);
    }

    @Test
    public void testValidateBadgeWhenNotAllowedAndBadgeUrlProvided() throws Exception {
        // not allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.SLIM_BANNER);
        when(blockDto.getKey()).thenReturn(MusicType.TRACK.name());
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);
        // not assigned
        when(blockDto.getBadgeUrl()).thenReturn("not-empty-badge-url");

        updateValidator.validateBadgeMapping(blockDto, errors);

        verify(errors).rejectValue("badgeUrl", "streamzine.error.badge.notallowed", null);
    }

    @Test
    public void testValidateUsersSuccess() throws Exception {
        UpdateIncomingDto updateIncomingDto = createUpdateIncomingDto("first", "second", "third");
        Errors errors = mock(Errors.class);
        // not allowed
        when(userRepository.findByUserNameAndCommunity(anyList(), eq("hl_uk"))).thenReturn(Lists.newArrayList(createUser("first"), createUser("third"), createUser("second")));

        updateValidator.validateUsers(updateIncomingDto, errors);

        verify(errors, never()).rejectValue("userNames", "streamzine.error.not.found.filtered.username", null);
    }

    @Test
    public void testValidateUsersFail() throws Exception {
        UpdateIncomingDto updateIncomingDto = createUpdateIncomingDto("first", "fourth", "third", "second");
        Errors errors = mock(Errors.class);
        // not allowed
        when(userRepository.findByUserNameAndCommunity(anyList(), eq("hl_uk"))).thenReturn(Lists.newArrayList(createUser("second"), createUser("first"), createUser("third")));

        updateValidator.validateUsers(updateIncomingDto, errors);

        verify(errors).rejectValue("userNames", "streamzine.error.not.found.filtered.username", null);
    }

    @Test
    public void shouldNotValidateWhenTracksAreTheSame(){
        // given
        OrdinalBlockDto musicTrackBlock = createMusicTypeBlock(MusicType.TRACK, "1");
        UpdateIncomingDto update = createUpdate(musicTrackBlock, musicTrackBlock);

        HashSet<Media> oneMedia = new HashSet<Media>(Arrays.asList(mock(Media.class)));
        when(mediaService.getMediasByChartAndPublishTimeAndMediaIds(any(String.class), anyLong(), anyList())).thenReturn(oneMedia);

        //when
        updateValidator.customValidate(update, errors);

        //then
        verify(errors, times(1)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldNotValidateWhenTracksAreNotTheSame(){
        // given
        OrdinalBlockDto musicTrackBlock1 = createMusicTypeBlock(MusicType.TRACK, "1");
        OrdinalBlockDto musicTrackBlock2 = createMusicTypeBlock(MusicType.TRACK, "2");
        UpdateIncomingDto update = createUpdate(musicTrackBlock1, musicTrackBlock2);

        HashSet<Media> oneMedia = new HashSet<Media>(Arrays.asList(mock(Media.class)));
        when(mediaService.getMediasByChartAndPublishTimeAndMediaIds(any(String.class), anyLong(), anyList())).thenReturn(oneMedia);

        //when
        updateValidator.customValidate(update, errors);

        //then
        verify(errors, times(0)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldNotValidateWhenPlaylistsAreTheSame(){
        // given
        OrdinalBlockDto musicPlaylistBlock = createMusicTypeBlock(MusicType.PLAYLIST, "SOME_PLAYLIST_1");
        UpdateIncomingDto update = createUpdate(musicPlaylistBlock, musicPlaylistBlock);

        //when
        updateValidator.customValidate(update, errors);

        //then
        verify(errors, times(1)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void shouldNotValidateWhenPlaylistsAreNotTheSame(){
        // given
        OrdinalBlockDto musicPlaylistBlock1 = createMusicTypeBlock(MusicType.PLAYLIST, "SOME_PLAYLIST_1");
        OrdinalBlockDto musicPlaylistBlock2 = createMusicTypeBlock(MusicType.PLAYLIST, "SOME_PLAYLIST_2");
        UpdateIncomingDto update = createUpdate(musicPlaylistBlock1, musicPlaylistBlock2);

        //when
        updateValidator.customValidate(update, errors);

        //then
        verify(errors, times(0)).rejectValue("value", "streamzine.error.duplicate.content", null);
    }

    @Test
    public void testValidateTitlesWhenNotAllowed() throws Exception {
        // titles not allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.SLIM_BANNER);
        when(blockDto.getTitle()).thenReturn("Achtung!");
        when(blockDto.getSubTitle()).thenReturn("Achtung!");

        updateValidator.validateTitlesMapping(blockDto, errors);

        verify(errors).rejectValue("title", "streamzine.error.title.not.allowed", null);
        verify(errors).rejectValue("subTitle", "streamzine.error.subtitle.not.allowed", null);
    }

    @Test
    public void testValidateTitlesWhenAllowed() throws Exception {
        // titles are allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.SLIM_BANNER);

        updateValidator.validateTitlesMapping(blockDto, errors);

        verify(errors, never()).rejectValue("title", "streamzine.error.title.not.allowed", null);
        verify(errors, never()).rejectValue("subTitle", "streamzine.error.subtitle.not.allowed", null);
    }

    @Test
    public void testValidateTitlesWhenAllowedButEmpty() throws Exception {
        // titles are allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getTitle()).thenReturn(null);
        when(blockDto.getSubTitle()).thenReturn("");

        updateValidator.validateTitlesValues(blockDto, errors);

        verify(errors).rejectValue("title", "streamzine.error.title.not.provided", null);
        verify(errors).rejectValue("subTitle", "streamzine.error.subtitle.not.provided", null);
    }

    @Test
    public void testValidateTitlesWhenAllowedButLong() throws Exception {
        // titles are allowed
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getTitle()).thenReturn(new String(new char[1000]));
        when(blockDto.getSubTitle()).thenReturn(new String(new char[1000]));

        updateValidator.validateTitlesValues(blockDto, errors);

        verify(errors).rejectValue("title", "streamzine.error.title.too.long", null);
        verify(errors).rejectValue("subTitle", "streamzine.error.subtitle.too.long", null);
    }

    private UpdateIncomingDto createUpdateIncomingDto(String... userNames) {
        UpdateIncomingDto updateIncomingDto = new UpdateIncomingDto();
        updateIncomingDto.setUserNames(Lists.newArrayList(userNames));
        return updateIncomingDto;
    }

    private User createUser(String userName) {
        User user = new User();
        user.setUserName(userName);
        return user;
    }

    private UpdateIncomingDto createUpdate(OrdinalBlockDto ... blocks) {
        UpdateIncomingDto updateIncomingDtoMock = mock(UpdateIncomingDto.class);
        when(updateIncomingDtoMock.getBlocks()).thenReturn(new ArrayList<OrdinalBlockDto>(Arrays.asList(blocks)));
        return updateIncomingDtoMock;
    }

    private OrdinalBlockDto createMusicTypeBlock(MusicType type, String value) {
        OrdinalBlockDto blockDto = mock(OrdinalBlockDto.class);
        when(blockDto.getShapeType()).thenReturn(ShapeType.WIDE);
        when(blockDto.getKey()).thenReturn(type.name());
        when(blockDto.provideKeyString()).thenReturn(type.name());
        when(blockDto.provideValueString()).thenReturn(value);
        when(blockDto.getValue()).thenReturn(value);
        when(blockDto.getContentType()).thenReturn(ContentType.MUSIC);
        when(blockDto.isIncluded()).thenReturn(true);
        return blockDto;
    }


}