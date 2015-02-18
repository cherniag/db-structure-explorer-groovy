package mobi.nowtechnologies.server.admin.settings.asm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import mobi.nowtechnologies.server.admin.settings.asm.BehaviorConfigTypeRules;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlist.PlaylistInfo;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.MetaInfo;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.PlaylistTypeInfoDto;
import mobi.nowtechnologies.server.admin.settings.asm.dto.referral.ReferralDto;
import mobi.nowtechnologies.server.dto.context.ContentBehaviorType;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.*;

@JsonTypeName("settings")
@XmlAccessorType(XmlAccessType.NONE)
public class SettingsDto {
    @JsonProperty(value = "enabled")
    private boolean enabled = true;

    @JsonProperty(value = "referral")
    private ReferralDto referralDto = new ReferralDto();

    @JsonProperty(value = "playlistSettings")
    private Map<Integer, Map<UserStatusType, PlaylistInfo>> playlistSettings = new HashMap<Integer, Map<UserStatusType, PlaylistInfo>>();

    @JsonProperty(value = "playlistInfo")
    private Map<Integer, ChartListItemDto> playlistInfo = new HashMap<Integer, ChartListItemDto>();

    @JsonProperty(value = "periods")
    private List<DurationUnit> periods = new ArrayList<DurationUnit>();

    @JsonProperty(value = "playlistTypeSettings")
    private Map<ChartBehaviorType, PlaylistTypeInfoDto> playlistTypeSettings = new HashMap<ChartBehaviorType, PlaylistTypeInfoDto>();

    @JsonProperty(value = "pages")
    private Set<String> pages = new HashSet<String>();

    @JsonProperty(value = "actions")
    private Set<String> actions = new HashSet<String>();

    @JsonProperty(value = "contentBehaviorTypes")
    private List<ContentBehaviorType> contentBehaviorTypes = new ArrayList<>();

    @JsonProperty(value = "chartTypes")
    private Set<ChartBehaviorType> chartBehaviorTypes = new HashSet<ChartBehaviorType>();

    @JsonProperty(value = "favourites")
    private Map<UserStatusType, ContentBehaviorType> favourites = new HashMap<>();

    @JsonProperty(value = "ads")
    private Map<UserStatusType, ContentBehaviorType> ads = new HashMap<>();

    @JsonProperty(value = "i18n")
    private Map<String, String> i18n = new HashMap<>();

    private transient BehaviorConfigType behaviorConfigType;

    public SettingsDto(BehaviorConfigType behaviorConfigType) {
        this.behaviorConfigType = behaviorConfigType;
        playlistTypeSettings.putAll(initPlaylistTypeMappings());
        favourites.putAll(initStatuses());
        ads.putAll(initStatuses());
        periods.addAll(getRequiredPeriodValues());
        chartBehaviorTypes.addAll(BehaviorConfigTypeRules.allowedChartBehaviorTypes(behaviorConfigType));
    }

    // for JSON
    public SettingsDto() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addPlaylistInfo(List<ChartListItemDto> chartListItemDtos) {
        for (ChartListItemDto chartListItemDto : chartListItemDtos) {
            playlistInfo.put(chartListItemDto.getChartId(), chartListItemDto);
        }
    }

    public Map<ChartBehaviorType, PlaylistTypeInfoDto> getPlaylistTypeSettings() {
        return playlistTypeSettings;
    }

    public Map<UserStatusType, PlaylistInfo> getPlaylistInfo(int chartId) {
        if(!playlistSettings.containsKey(chartId)) {
            playlistSettings.put(chartId, createUserStatusTypePlaylistInfo());
        }
        return playlistSettings.get(chartId);
    }

    public Map<Integer, Map<UserStatusType, PlaylistInfo>> getPlaylistSettings() {
        return playlistSettings;
    }

    public void setPages(Set<String> pages) {
        this.pages.addAll(pages);
    }

    public void setActions(Set<String> actions) {
        this.actions.addAll(actions);
    }

    public void setContentBehaviorTypes(List<ContentBehaviorType> contentBehaviorTypes) {
        this.contentBehaviorTypes.addAll(contentBehaviorTypes);
    }

    public ReferralDto getReferralDto() {
        return referralDto;
    }

    public Map<UserStatusType, ContentBehaviorType> getFavourites() {
        return favourites;
    }

    public Map<UserStatusType, ContentBehaviorType> getAds() {
        return ads;
    }

    public void addLocalizationInfo(Map<String, String> localizationData) {
        i18n.putAll(localizationData);
    }

    public void setMetaInfo(Map<ChartBehaviorType, MetaInfo> metaInfo) {
        for (Map.Entry<ChartBehaviorType, MetaInfo> chartBehaviorTypeMetaInfoEntry : metaInfo.entrySet()) {
            ChartBehaviorType key = chartBehaviorTypeMetaInfoEntry.getKey();
            MetaInfo value = chartBehaviorTypeMetaInfoEntry.getValue();

            playlistTypeSettings.get(key).getMetaInfo().setTracksInfoSupported(value.isTracksInfoSupported());
            playlistTypeSettings.get(key).getMetaInfo().setTracksPlayDurationSupported(value.isTracksPlayDurationSupported());
        }
    }

    //
    // Internals
    //
    private List<DurationUnit> getRequiredPeriodValues() {
        List<DurationUnit> durationUnits = new ArrayList<DurationUnit>(Arrays.asList(DurationUnit.values()));
        durationUnits.removeAll(Arrays.asList(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.MONTHS, DurationUnit.YEARS));
        return durationUnits;
    }

    private HashMap<UserStatusType, PlaylistInfo> createUserStatusTypePlaylistInfo() {
        HashMap<UserStatusType, PlaylistInfo> mapping = new HashMap<UserStatusType, PlaylistInfo>();
        for (UserStatusType userStatusType : BehaviorConfigTypeRules.allowedUserStatusTypes(behaviorConfigType)) {
            mapping.put(userStatusType, new PlaylistInfo());
        }
        return mapping;
    }

    private Map<ChartBehaviorType, PlaylistTypeInfoDto> initPlaylistTypeMappings() {
        Map<ChartBehaviorType, PlaylistTypeInfoDto> playlistType = new HashMap<ChartBehaviorType, PlaylistTypeInfoDto>();
        for (ChartBehaviorType type : BehaviorConfigTypeRules.allowedChartBehaviorTypes(behaviorConfigType)) {
            playlistType.put(type, new PlaylistTypeInfoDto());
        }
        return playlistType;
    }

    private Map<UserStatusType, ContentBehaviorType> initStatuses() {
        Map<UserStatusType, ContentBehaviorType> statuses = new HashMap<>();
        for (UserStatusType userStatusType : UserStatusType.values()) {
            statuses.put(userStatusType, ContentBehaviorType.DISABLED);
        }
        return statuses;
    }
}
