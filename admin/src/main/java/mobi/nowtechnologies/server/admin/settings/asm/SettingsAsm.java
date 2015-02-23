package mobi.nowtechnologies.server.admin.settings.asm;

import mobi.nowtechnologies.server.admin.settings.asm.dto.SettingsDto;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.MetaInfo;
import mobi.nowtechnologies.server.admin.settings.service.SettingsService;
import mobi.nowtechnologies.server.dto.context.ContentBehaviorType;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

public class SettingsAsm {
    private MessageSource messageSource;
    private SettingsService settingsService;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public SettingsDto createDto(String communityRewriteUrl, List<ChartListItemDto> chartListItemDtos, Set<String> pages, Set<String> actions) {
        SettingsDto dto = settingsService.export(communityRewriteUrl);

        if(dto == null) {
            return null;
        }

        // add some dictionary data
        dto.setPages(pages);
        dto.setMetaInfo(createMetaInfo(dto.getBehaviorConfigType()));
        dto.setActions(actions);
        dto.setContentBehaviorTypes(Arrays.asList(ContentBehaviorType.values()));
        dto.addPlaylistInfo(chartListItemDtos);
        dto.addLocalizationInfo(getLocalizationData());
        return dto;
    }

    private Map<ChartBehaviorType, MetaInfo> createMetaInfo(BehaviorConfigType behaviorConfigType) {
        Map<ChartBehaviorType, MetaInfo> map = new HashMap<ChartBehaviorType, MetaInfo>();
        for (ChartBehaviorType chartBehaviorType : BehaviorConfigTypeRules.allowedChartBehaviorTypes(behaviorConfigType)) {
            MetaInfo info = new MetaInfo();
            info.setTracksInfoSupported(chartBehaviorType.isTracksInfoSupported());
            info.setTracksPlayDurationSupported(chartBehaviorType.isTracksPlayDurationSupported());
            map.put(chartBehaviorType, info);
        }
        return map;
    }

    private Map<String, String> getLocalizationData() {
        Map<String, String> i18n = new HashMap<String, String>();
        final String domainPrefix = "settings.";
        for (String code : dictionaryLocCodes()) {
            i18n.put(code, messageSource.getMessage(domainPrefix + code, null, LocaleContextHolder.getLocale()));
        }
        return i18n;
    }

    private Set<String> dictionaryLocCodes() {
        Set<String> keys = new HashSet<String>();
        for (ChartBehaviorType playlistType : ChartBehaviorType.values()) {
            keys.add(playlistType.name());
        }
        for (UserStatusType userStatusType : UserStatusType.values()) {
            keys.add(userStatusType.name());
        }
        return keys;
    }
}
