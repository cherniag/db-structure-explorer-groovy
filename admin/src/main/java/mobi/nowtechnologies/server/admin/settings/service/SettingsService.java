package mobi.nowtechnologies.server.admin.settings.service;

import mobi.nowtechnologies.server.admin.settings.asm.BehaviorConfigTypeRules;
import mobi.nowtechnologies.server.admin.settings.asm.dto.SettingsDto;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlist.PlaylistInfo;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.PlaylistTypeInfoDto;
import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.TracksInfoDto;
import mobi.nowtechnologies.server.dto.context.ContentBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.*;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.BehaviorConfigRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class SettingsService {
    private CommunityRepository communityRepository;
    private ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository;
    private CommunityConfigRepository communityConfigRepository;
    private BehaviorConfigRepository behaviorConfigRepository;

    @Transactional(readOnly = true)
    public SettingsDto export(String communityUrl) {
        Community c = communityRepository.findByRewriteUrlParameter(communityUrl);
        Assert.notNull(c);

        CommunityConfig communityConfig = communityConfigRepository.findByCommunity(c);

        if(communityConfig == null) {
            return null;
        }

        final BehaviorConfigType behaviorConfigType = communityConfig.getBehaviorConfig().getType();
        SettingsDto dto = new SettingsDto(behaviorConfigType);

        final BehaviorConfig currentBehaviorConfig = communityConfig.getBehaviorConfig();

        dto.getReferralDto().setRequired(currentBehaviorConfig.getRequiredReferrals());
        dto.getReferralDto().getDurationInfoDto().fromDuration(currentBehaviorConfig.getReferralsDuration());


        for (ChartBehaviorType chartBehaviorType : BehaviorConfigTypeRules.allowedChartBehaviorTypes(behaviorConfigType)) {
            final ChartBehavior chartBehavior = currentBehaviorConfig.getChartBehavior(chartBehaviorType);

            PlaylistTypeInfoDto playlistTypeInfoDto = dto.getPlaylistTypeSettings().get(chartBehaviorType);
            playlistTypeInfoDto.setPlayTrackSeconds(chartBehavior.getPlayTracksSeconds());
            playlistTypeInfoDto.setOffline(chartBehavior.isOffline());
            playlistTypeInfoDto.getMaxTracks().setNumber(chartBehavior.getMaxTracks());
            playlistTypeInfoDto.getMaxTracks().getDurationInfoDto().fromDuration(chartBehavior.getMaxTracksDuration());
            playlistTypeInfoDto.getSkipTracks().setNumber(chartBehavior.getSkipTracks());
            playlistTypeInfoDto.getSkipTracks().getDurationInfoDto().fromDuration(chartBehavior.getSkipTracksDuration());
        }


        List<UserStatusType> allowedUserStatusTypes = BehaviorConfigTypeRules.allowedUserStatusTypes(behaviorConfigType);

        for (UserStatusType userStatusType : UserStatusType.values()) {
            final ContentUserStatusBehavior contentUserStatusBehavior = currentBehaviorConfig.getContentUserStatusBehavior(userStatusType);

            dto.getAds().put(userStatusType, ContentBehaviorType.valueOf(contentUserStatusBehavior.isAddsOff()));
            dto.getFavourites().put(userStatusType, ContentBehaviorType.valueOf(contentUserStatusBehavior.isFavoritesOff()));
        }

        List<ChartUserStatusBehavior> behaviors = chartUserStatusBehaviorRepository.findByBehaviorConfig(currentBehaviorConfig, allowedUserStatusTypes);

        for (ChartUserStatusBehavior behavior : behaviors) {
            final int chartId = behavior.getChartId();

            Map<UserStatusType, PlaylistInfo> integerPlaylistInfoMap = dto.getPlaylistInfo(chartId);

            for (UserStatusType userStatusType : allowedUserStatusTypes) {
                ChartUserStatusBehavior chartBehaviorForStatus = chartUserStatusBehaviorRepository.findByChartIdBehaviorConfigAndStatus(chartId, currentBehaviorConfig, userStatusType);
                integerPlaylistInfoMap.get(userStatusType).setAction(chartBehaviorForStatus.getAction());
                integerPlaylistInfoMap.get(userStatusType).setChartBehaviorType(chartBehaviorForStatus.getChartBehavior().getType());
            }
        }
        return dto;
    }

    @Transactional
    public void switchConfigType(String communityUrl, BehaviorConfigType newBehaviorConfigType){
        Community c = communityRepository.findByRewriteUrlParameter(communityUrl);
        Assert.notNull(c);
        CommunityConfig communityConfig = communityConfigRepository.findByCommunity(c);

        if (communityConfig.requiresBehaviorConfigChange(newBehaviorConfigType)) {
            BehaviorConfig newBehaviorConfig = behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(c.getId(), newBehaviorConfigType);
            communityConfig.setBehaviorConfig(newBehaviorConfig);
            logger().info("User switched for {} community to new behavior config type {}", communityUrl, newBehaviorConfigType);
        }
    }


    @Transactional
    public void makeImport(String communityUrl, SettingsDto dto) {
        logger().info("Saving from UI settings for {}", communityUrl);

        Community c = communityRepository.findByRewriteUrlParameter(communityUrl);
        Assert.notNull(c);

        // Update current behavior config
        BehaviorConfig currentBehaviorConfig = behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(c.getId(), dto.getBehaviorConfigType());

        Duration referralDuration = dto.getReferralDto().getDurationInfoDto().toDuration();
        int requiredAmount = dto.getReferralDto().getRequired();
        currentBehaviorConfig.updateReferralsInfo(requiredAmount, referralDuration);

        for (ChartBehaviorType chartBehaviorType : BehaviorConfigTypeRules.allowedChartBehaviorTypes(currentBehaviorConfig.getType())) {
            int playSeconds = dto.getPlaylistTypeSettings().get(chartBehaviorType).getPlayTrackSeconds();
            boolean offline = dto.getPlaylistTypeSettings().get(chartBehaviorType).isOffline();
            TracksInfoDto maxTracks = dto.getPlaylistTypeSettings().get(chartBehaviorType).getMaxTracks();
            TracksInfoDto skipTracks = dto.getPlaylistTypeSettings().get(chartBehaviorType).getSkipTracks();

            ChartBehavior chartBehavior = currentBehaviorConfig.getChartBehavior(chartBehaviorType);
            chartBehavior.setOffline(offline);
            if (chartBehaviorType.isTracksPlayDurationSupported()) {
                chartBehavior.setPlayTracksSeconds(playSeconds);
            }
            if (chartBehaviorType.isTracksInfoSupported()) {
                chartBehavior.updateMaxTracksInfo(maxTracks.getNumber(), maxTracks.getDurationInfoDto().toDuration());
                chartBehavior.updateSkipTracksInfo(skipTracks.getNumber(), skipTracks.getDurationInfoDto().toDuration());
            }
        }

        for (UserStatusType userStatusType : UserStatusType.values()) {
            ContentBehaviorType ads = dto.getAds().get(userStatusType);
            ContentBehaviorType fav = dto.getFavourites().get(userStatusType);

            ContentUserStatusBehavior contentUserStatusBehavior = currentBehaviorConfig.getContentUserStatusBehavior(userStatusType);
            contentUserStatusBehavior.setAddsOff(ads.isOff());
            contentUserStatusBehavior.setFavoritesOff(fav.isOff());
        }

        for (Map.Entry<Integer, Map<UserStatusType, PlaylistInfo>> chartToInfoMappingEntry : dto.getPlaylistSettings().entrySet()) {
            final int chartId = chartToInfoMappingEntry.getKey();

            for (Map.Entry<UserStatusType, PlaylistInfo> userStatusTypePlaylistInfoEntry : chartToInfoMappingEntry.getValue().entrySet()) {
                UserStatusType userStatusType = userStatusTypePlaylistInfoEntry.getKey();
                final PlaylistInfo playlistInfo = userStatusTypePlaylistInfoEntry.getValue();
                final ChartBehavior selectedChartBehavior = currentBehaviorConfig.getChartBehavior(playlistInfo.getChartBehaviorType());

                ChartUserStatusBehavior chartUserStatusBehavior = chartUserStatusBehaviorRepository.findByChartIdBehaviorConfigAndStatus(chartId, currentBehaviorConfig, userStatusType);
                chartUserStatusBehavior.setChartBehavior(selectedChartBehavior);
                chartUserStatusBehavior.setAction(playlistInfo.getAction());
            }
        }
    }

    //
    // ioc setters
    //
    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public void setChartUserStatusBehaviorRepository(ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository) {
        this.chartUserStatusBehaviorRepository = chartUserStatusBehaviorRepository;
    }

    public void setCommunityConfigRepository(CommunityConfigRepository communityConfigRepository) {
        this.communityConfigRepository = communityConfigRepository;
    }

    public void setBehaviorConfigRepository(BehaviorConfigRepository behaviorConfigRepository) {
        this.behaviorConfigRepository = behaviorConfigRepository;
    }

    //
    // internals
    //
    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
