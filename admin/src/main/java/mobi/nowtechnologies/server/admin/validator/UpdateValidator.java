package mobi.nowtechnologies.server.admin.validator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.streamzine.DuplicatedContentKey;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.internal.util.Assert;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

            if (blockDto.isIncluded()) {
                baseValidate(blockDto, errors);
                validateValue(dto, errors, blockDto);
                validateDuplicatedContent(blockDto, errors, isrcs, playlists);
            }

            errors.popNestedPath();
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
        boolean allowed = BadgeMappingRules.allowed(blockDto.getShapeType(), blockDto.getContentType(), subType);

        if(!allowed && isNotEmpty(blockDto.getBadgeUrl())) {
            String shapeType = getShapeTypeTitle(blockDto);
            String contentType = getContentTypeTitle(blockDto);
            String subTypeValue = getSubTypeTitle(blockDto);

            rejectField("streamzine.error.badge.notallowed", new Object[]{shapeType, contentType, subTypeValue}, errors, "badgeUrl");
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
            DeepLinkInfoService.ApplicationPageData applicationPageData = new DeepLinkInfoService.ApplicationPageData(value);

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
            try {
                URI uri = URI.create(value);
                Assert.notNull(uri.getScheme());
                Assert.notNull(uri.getHost());
            } catch (IllegalArgumentException e) {
                Object[] args = {value};
                rejectValue("streamzine.error.notvalid.url", args, errors);
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
            try {
                ChartType.valueOf(value);
            } catch (IllegalArgumentException e) {
                Object[] args = {value, Arrays.toString(ChartType.values())};
                rejectValue("streamzine.error.notfound.playlist.id", args, errors);
            }
            return;
        }

        if(musicType == MusicType.TRACK) {
            Set<Media> media = mediaService.getMediasByChartAndPublishTimeAndMediaIsrcs(communityRewriteUrl, publishTimeMillis, Lists.newArrayList(value));
            boolean notFoundMedia = (media == null || media.size() == 0);
            if(notFoundMedia) {
                Object[] args = {value};
                rejectValue("streamzine.error.notfound.track.id", args, errors);
            }
            return;
        }

        if(musicType == MusicType.MANUAL_COMPILATION) {
            DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(value);
            List<String> mediaIsrcs = manualCompilationData.getMediaIsrcs();
            Set<Media> medias = mediaService.getMediasByChartAndPublishTimeAndMediaIsrcs(communityRewriteUrl, publishTimeMillis, mediaIsrcs);
            if(medias.size() != mediaIsrcs.size()){
                for (Media media : medias) {
                    mediaIsrcs.remove(media.getIsrc());
                }
                rejectValue("streamzine.error.notfound.manual.compilation.isrc", new String[]{mediaIsrcs.toString()}, errors);
            }
            return;
        }

        throw new IllegalArgumentException("No validation for music type: " + musicType);
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

            Message message = messageRepository.findOne(Integer.parseInt(value));
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
