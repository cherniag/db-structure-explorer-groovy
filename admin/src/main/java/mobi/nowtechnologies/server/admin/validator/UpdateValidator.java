package mobi.nowtechnologies.server.admin.validator;

import com.google.common.annotations.VisibleForTesting;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.ApplicationPageData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.streamzine.DuplicatedContentKey;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasSubTitle;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasTitle;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.isEmpty;

public class UpdateValidator extends BaseValidator {
    @Resource
    private MessageSource messageSource;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private MediaService mediaService;
    @Resource
    private MobileApplicationPagesService mobileApplicationPagesService;
    @Resource
    private StreamzineTypesMappingService streamzineTypesMappingService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private CookieUtil cookieUtil;
    @Resource
    private FilenameAliasRepository filenameAliasRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateIncomingDto.class.isAssignableFrom(clazz);
    }

    @Override
    @Transactional(readOnly = true)
    protected boolean customValidate(Object target, Errors errors) {
        validateValues((UpdateIncomingDto) target, errors);
        return errors.hasErrors();
    }

    private void validateValues(UpdateIncomingDto dto, Errors errors) {
        Collections.sort(dto.getBlocks(), OrdinalBlockDto.COMPARATOR);
        dto.removeUserNameDuplicates();

        validateUsers(dto, errors);

        final Map<DuplicatedContentKey, OrdinalBlockDto> isrcs = new HashMap<DuplicatedContentKey, OrdinalBlockDto>();
        final Map<DuplicatedContentKey, OrdinalBlockDto> playlists = new HashMap<DuplicatedContentKey, OrdinalBlockDto>();

        for (int index = 0; index < dto.getBlocks().size(); index++) {
            OrdinalBlockDto blockDto = dto.getBlocks().get(index);

            errors.pushNestedPath("blocks[" + index + "]");

            // should be validated even if not included
            validateMapping(blockDto, errors);
            validateBadgeMapping(blockDto, errors);
            validateTitlesMapping(blockDto, errors);

            if (blockDto.isIncluded()) {
                baseValidate(blockDto, errors);
                validateValue(dto, errors, blockDto);
                validateDuplicatedContent(blockDto, errors, isrcs, playlists);
                validateTitlesValues(blockDto, errors);
            }

            errors.popNestedPath();
        }
    }

    @VisibleForTesting
    void validateTitlesMapping(OrdinalBlockDto blockDto, Errors errors) {
        ShapeType shapeType = blockDto.getShapeType();
        if(!hasTitle(shapeType) && hasLength(blockDto.getTitle())){
            rejectField("streamzine.error.title.not.allowed", new Object[]{shapeType}, errors, "title");
        }

        if(!hasSubTitle(shapeType) && hasLength(blockDto.getSubTitle())){
            rejectField("streamzine.error.subtitle.not.allowed", new Object[]{shapeType}, errors, "subTitle");
        }
    }

    @VisibleForTesting
    void validateTitlesValues(OrdinalBlockDto blockDto, Errors errors) {
        ShapeType shapeType = blockDto.getShapeType();
        if (hasTitle(shapeType)) {
            if (isEmpty(blockDto.getTitle())) {
                rejectField("streamzine.error.title.not.provided", new Object[]{}, errors, "title");
            } else if(blockDto.getTitle().length()> Block.TITLE_MAX_LENGTH){
                rejectField("streamzine.error.title.too.long", new Object[]{Block.TITLE_MAX_LENGTH}, errors, "title");
            }
        }

        if (hasSubTitle(shapeType)) {
            if (isEmpty(blockDto.getSubTitle())) {
                rejectField("streamzine.error.subtitle.not.provided", new Object[]{}, errors, "subTitle");
            } else if(blockDto.getSubTitle().length()> Block.SUBTITLE_MAX_LENGTH){
                rejectField("streamzine.error.subtitle.too.long", new Object[]{Block.SUBTITLE_MAX_LENGTH}, errors, "subTitle");
            }
        }
    }

    @VisibleForTesting
    void validateUsers(UpdateIncomingDto dto, Errors errors) {
        List<String> userNames = dto.getUserNames();
        if(userNames.isEmpty()){
            return;
        }
        String communityRewriteUrl = cookieUtil.get(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        List<User> found = userRepository.findByUserNameAndCommunity(userNames, communityRewriteUrl);

        removeFoundInDatabaseFromIncoming(userNames, found);

        if(!userNames.isEmpty()) {
            rejectField("streamzine.error.not.found.filtered.username", new Object[]{userNames.toString(), communityRewriteUrl}, errors, "userNames");
        }
    }

    private void removeFoundInDatabaseFromIncoming(List<String> userNames, List<User> found) {
        for(User user: found){
            userNames.remove(user.getUserName());
        }
    }

    private void validateDuplicatedContent(OrdinalBlockDto blockDto, Errors errors, Map<DuplicatedContentKey, OrdinalBlockDto> isrcs, Map<DuplicatedContentKey, OrdinalBlockDto> playlists) {
        if(blockDto.getContentType() == ContentType.MUSIC) {
            final MusicType musicType = MusicType.valueOf(blockDto.getKey());
            final DuplicatedContentKey contentKey = new DuplicatedContentKey(blockDto);

            if(musicType == MusicType.PLAYLIST) {
                if(playlists.containsKey(contentKey)) {
                    OrdinalBlockDto first = playlists.get(contentKey);
                    rejectField("streamzine.error.duplicate.content", new Object[]{blockDto.getTitle(), first.getTitle()}, errors, "value");
                } else {
                    playlists.put(contentKey, blockDto);
                }
            }

            if(musicType == MusicType.TRACK) {
                if(isrcs.containsKey(contentKey)) {
                    OrdinalBlockDto first = isrcs.get(contentKey);
                    rejectField("streamzine.error.duplicate.content", new Object[]{blockDto.getTitle(), first.getTitle()}, errors, "value");
                } else {
                    isrcs.put(contentKey, blockDto);
                }
            }
        }
    }

    private void validateValue(UpdateIncomingDto dto, Errors errors, OrdinalBlockDto blockDto) {
        switch (blockDto.getContentType()) {
            case NEWS:
                validateNews(blockDto, errors);
                return;
            case MUSIC:
                validateMusic(blockDto, errors, dto.getTimestamp());
                return;
            case PROMOTIONAL:
                validatePromotional(blockDto, errors);
        }
    }

    @VisibleForTesting
    void validateBadgeMapping(OrdinalBlockDto blockDto, Errors errors) {
        Enum<?> subType = TypeToSubTypePair.restoreSubType(blockDto.getContentType(), blockDto.getKey());

        if(blockDto.getBadgeId() != null) {
            // check allowance
        boolean allowed = BadgeMappingRules.allowed(blockDto.getShapeType(), blockDto.getContentType(), subType);
            if(!allowed) {
            String shapeType = getShapeTypeTitle(blockDto);
            String contentType = getContentTypeTitle(blockDto);
            String subTypeValue = getSubTypeTitle(blockDto);

                rejectField("streamzine.error.badge.notallowed", new Object[]{shapeType, contentType, subTypeValue}, errors, "badgeId");
            }

            // check existence
            FilenameAlias found = filenameAliasRepository.findOne(blockDto.getBadgeId());
            if(found == null) {
                rejectField("streamzine.error.badge.absent", new Object[]{blockDto.getBadgeId()}, errors, "badgeId");
            }
        }
    }

    private void validateMapping(DeeplinkInfoData blockDto, Errors errors) {
        TypesMappingInfo info = streamzineTypesMappingService.getTypesMappingInfos();

        if(!info.matches(blockDto)) {
            String shapeType = getShapeTypeTitle(blockDto);
            String contentType = getContentTypeTitle(blockDto);
            String subTypeValue = getSubTypeTitle(blockDto);

            rejectField("streamzine.error.types.mapping", new Object[]{shapeType, contentType, subTypeValue}, errors, "contentType");
        }
    }

    private void validatePromotional(OrdinalBlockDto blockDto, Errors errors) {
        final String key = blockDto.provideKeyString();
        final String value = blockDto.provideValueString();

        LinkLocationType linkLocationType = LinkLocationType.valueOf(key);

        if(linkLocationType == LinkLocationType.INTERNAL_AD) {
            ApplicationPageData applicationPageData = new ApplicationPageData(value);

            final Set<String> pages = mobileApplicationPagesService.getPages();
            if(!pages.contains(applicationPageData.getUrl())) {
                Object[] args = {value, pages.toString()};
                rejectValue("streamzine.error.unknown.appurl", args, errors);
                return;
            }

            if(!applicationPageData.getAction().isEmpty()) {
                final Set<String> actions = mobileApplicationPagesService.getActions();
                if(!actions.contains(applicationPageData.getAction())) {
                    Object[] args = {value, pages.toString()};
                    rejectValue("streamzine.error.unknown.appaction", args, errors);
                    return;
                }
            }
            return;
        }

        if(linkLocationType == LinkLocationType.EXTERNAL_AD) {
            String url = "";
            try {
                url = blockDto.getValueLink();
                URI uri = URI.create(url);
                Assert.notNull(uri.getScheme());
                Assert.notNull(uri.getHost());
            } catch (IllegalArgumentException e) {
                Object[] args = {url};
                rejectField("streamzine.error.notvalid.url", args, errors, "valueLink");
            }


            String openerAsString = "";
            try {
                openerAsString = blockDto.getValueOpener();
                Opener.valueOf(openerAsString);
            } catch (IllegalArgumentException e) {
                Object[] args = {openerAsString};
                rejectField("streamzine.error.notvalid.opener", args, errors, "valueOpener");
            }

            return;
        }

        throw new IllegalArgumentException("No validation for link location type: " + linkLocationType);
    }

    //
    // Internal validations
    //
    private void validateMusic(OrdinalBlockDto blockDto, Errors errors, long publishTimeMillis) {
        final String communityRewriteUrl = cookieUtil.get(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);

        final String key = blockDto.provideKeyString();
        final String value = blockDto.provideValueString();

        MusicType musicType = MusicType.valueOf(key);

        if(musicType == MusicType.PLAYLIST) {
            PlaylistData playlistData = new PlaylistData(value);
            try {
                parseInt(playlistData.getChartIdString());
            } catch (IllegalArgumentException e) {
                Object[] args = {value, Arrays.toString(ChartType.values())};
                rejectValue("streamzine.error.notfound.playlist.id", args, errors);
            }
            validatePlayerType(errors, playlistData.getPlayerTypeString());
            return;
        }

        if(musicType == MusicType.TRACK) {
            TrackData trackData = new TrackData(value);
            Set<Media> mediaSet = mediaService.getMediasByChartAndPublishTimeAndMediaIds(communityRewriteUrl, publishTimeMillis, newArrayList(trackData.getMediaId()));
            boolean mediaSetIsEmpty = CollectionUtils.isEmpty(mediaSet);
            if(mediaSetIsEmpty) {
                rejectValue("streamzine.error.notfound.track.id", new Object[]{value}, errors);
            }
            validatePlayerType(errors, trackData.getPlayerTypeString());
            return;
        }

        if(musicType == MusicType.MANUAL_COMPILATION) {
            DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(value);
            List<Integer> ids = manualCompilationData.getMediaIds();
            Set<Media> medias = mediaService.getMediasByChartAndPublishTimeAndMediaIds(communityRewriteUrl, publishTimeMillis, ids);
            if(medias.size() != ids.size()){
                for (Media media : medias) {
                    ids.remove(media.getIsrc());
                }
                rejectValue("streamzine.error.notfound.manual.compilation.isrc", new String[]{ids.toString()}, errors);
            }
            return;
        }

        throw new IllegalArgumentException("No validation for music type: " + musicType);
    }

    private void validatePlayerType(Errors errors,  String playerType) {
        if (isNull(playerType)){
            rejectValue("streamzine.error.no.playerType", new Object[]{playerType}, errors);
        }
        try {
            PlayerType.valueOf(playerType);
        } catch (IllegalArgumentException e) {
            Object[] args = {playerType, Arrays.toString(PlayerType.values())};
            rejectValue("streamzine.error.unknown.playerType", args, errors);
        }
    }

    private void validateNews(OrdinalBlockDto blockDto, Errors errors) {
        final String key = blockDto.provideKeyString();
        final String value = blockDto.provideValueString();

        NewsType newsType = NewsType.valueOf(key);

        if(newsType == NewsType.LIST) {
            boolean notTimestamp = !NumberUtils.isNumber(value);
            if(notTimestamp) {
                Object[] args = {value};
                rejectValue("streamzine.error.notvalid.timestamp", args, errors);
            }
            return;
        }

        if(newsType == NewsType.STORY) {
            boolean notId = !NumberUtils.isNumber(value);
            if(notId) {
                Object[] args = {value};
                rejectValue("streamzine.error.notfound.news.id", args, errors);
                return;
            }

            Message message = messageRepository.findOne(parseInt(value));
            boolean notFoundMessage = (message == null);
            if(notFoundMessage) {
                Object[] args = {value};
                rejectValue("streamzine.error.notfound.news.id", args, errors);
            }
            return;
        }

        throw new IllegalArgumentException("No validation for news type: " + newsType);
    }

    //
    // Helpers
    //
    private void rejectValue(String code, Object[] args, Errors errors) {
        rejectField(code, args, errors, "value");
    }

    private void rejectField(String code, Object[] args, Errors errors, String field) {
        String errorMessage = messageSource.getMessage(code, args, getLocale());
        errors.rejectValue(field, code, errorMessage);
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    private String getSubTypeTitle(DeeplinkInfoData blockDto) {
        return messageSource.getMessage("streamzine.contenttype." + blockDto.getContentType().name() + "." + blockDto.getKey(), null, getLocale());
    }

    private String getContentTypeTitle(DeeplinkInfoData blockDto) {
        return messageSource.getMessage("streamzine.contenttype." + blockDto.getContentType().name(), null, getLocale());
    }

    private String getShapeTypeTitle(DeeplinkInfoData blockDto) {
        return messageSource.getMessage("streamzine.shapetype." + blockDto.getShapeType().name(), null, getLocale());
    }
}
