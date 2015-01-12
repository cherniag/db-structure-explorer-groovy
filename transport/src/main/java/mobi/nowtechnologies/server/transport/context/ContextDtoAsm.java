package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.*;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import mobi.nowtechnologies.server.service.behavior.UserStatusTypeService;
import mobi.nowtechnologies.server.transport.context.dto.*;

import javax.annotation.Resource;
import java.util.*;

public class ContextDtoAsm {
    @Resource
    UserStatusTypeService userStatusTypeService;
    @Resource
    ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository;
    @Resource
    DeepLinkUrlFactory deepLinkUrlFactory;
    @Resource
    UserReferralsSnapshotRepository userReferralsSnapshotRepository;
    @Resource
    CommunityConfigRepository communityConfigRepository;

    //
    // API
    //
    public ContextDto assemble() {
        return ContextDto.empty();
    }

    public ContextDto assemble(User user, UserReferralsSnapshot snapshot, Date serverTime) {
        Community community = user.getCommunity();

        CommunityConfig communityConfig = communityConfigRepository.findByCommunity(community);
        BehaviorConfig behaviorConfig = communityConfig.getBehaviorConfig();

        String rewriteUrlParameter = community.getRewriteUrlParameter();

        Map<UserStatusType, Date> userStatusTypeDateMap = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        ContextDto context = ContextDto.normal(serverTime);

        fillReferralsInfo(context, snapshot);
        fillChartsTemplatesInfo(context, behaviorConfig);

        context.getFavoritesContextDto().getInstructions().addAll(
                calcFavouritesInstructions(behaviorConfig, userStatusTypeDateMap)
        );
        context.getAdsContextDto().getInstructions().addAll(
                calcAdsInstructions(behaviorConfig, userStatusTypeDateMap)
        );

        ReferralsConditions referralsConditions = getReferralsConditions(snapshot);
        context.getChartContextDto().getChartBehaviors().addAll(
                calcChartBehaviors(behaviorConfig, userStatusTypeDateMap, referralsConditions, rewriteUrlParameter)
        );

        return context;
    }

    //
    // Internal parts
    //
    private void fillReferralsInfo(ContextDto context, UserReferralsSnapshot snapshot) {
        if (snapshot != null) {
            context.getReferralsContextDto().fill(snapshot);
        }
    }

    private void fillChartsTemplatesInfo(ContextDto context, BehaviorConfig behaviorConfig) {
        for (ChartBehaviorType chartBehaviorType : ChartBehaviorType.values()) {
            ChartBehavior chartBehavior = behaviorConfig.getChartBehavior(chartBehaviorType);

            BehaviorTemplateDto behaviorTemplateDto = context.getChartContextDto().getChartTemplateBehaviorsDto(chartBehaviorType);
            behaviorTemplateDto.fill(chartBehavior);
        }
    }

    private List<InstructionDto> calcFavouritesInstructions(BehaviorConfig behaviorConfig, Map<UserStatusType, Date> userStatusTypeDateMap) {
        List<InstructionDto> instructions = new ArrayList<InstructionDto>(2);

        for (Map.Entry<UserStatusType, Date> statusTypeDateEntry : userStatusTypeDateMap.entrySet()) {
            UserStatusType statusType = statusTypeDateEntry.getKey();
            Date since = statusTypeDateEntry.getValue();

            boolean favouritesOff = behaviorConfig.getContentUserStatusBehavior(statusType).isFavoritesOff();
            instructions.add(new InstructionDto(favouritesOff, since));
        }

        return instructions;
    }

    private List<InstructionDto> calcAdsInstructions(BehaviorConfig behaviorConfig, Map<UserStatusType, Date> userStatusTypeDateMap) {
        List<InstructionDto> instructions = new ArrayList<InstructionDto>(2);

        for (Map.Entry<UserStatusType, Date> statusTypeDateEntry : userStatusTypeDateMap.entrySet()) {
            UserStatusType statusType = statusTypeDateEntry.getKey();
            boolean addsOff = behaviorConfig.getContentUserStatusBehavior(statusType).isAddsOff();
            instructions.add(new InstructionDto(addsOff, statusTypeDateEntry.getValue()));
        }

        return instructions;
    }

    private List<ChartBehaviorsDto> calcChartBehaviors(BehaviorConfig behaviorConfig, Map<UserStatusType, Date> userStatusTypeDateMap, ReferralsConditions conditions, String community) {
        Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> orderedByChartIdAndUserStatusType = chartToStatusMapping(behaviorConfig);

        List<ChartBehaviorsDto> chartBehaviors = new ArrayList<ChartBehaviorsDto>(orderedByChartIdAndUserStatusType.size());

        for (Map.Entry<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusesEntry : orderedByChartIdAndUserStatusType.entrySet()) {
            ChartBehaviorsDto chartBehaviorsDto = createChartBehaviorsDto(userStatusTypeDateMap, community, chartToStatusesEntry);
            conditions.unlockChartBehaviors(chartBehaviorsDto.getBehavior());

            chartBehaviors.add(
                    chartBehaviorsDto
            );
        }

        return chartBehaviors;
    }

    //
    // Helpers
    //
    private ChartBehaviorsDto createChartBehaviorsDto(Map<UserStatusType, Date> userStatusTypeDateMap, String community,
                                                      Map.Entry<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusesEntry) {
        int chartId = chartToStatusesEntry.getKey();
        Map<UserStatusType, ChartUserStatusBehavior> userStatusToChartTypeMapping = chartToStatusesEntry.getValue();

        ChartBehaviorsDto result = new ChartBehaviorsDto(chartId);
        for (Map.Entry<UserStatusType, Date> userStatusTypeDateEntry : userStatusTypeDateMap.entrySet()) {
            UserStatusType userStatus = userStatusTypeDateEntry.getKey();
            Date validFrom = userStatusTypeDateEntry.getValue();
            result.getBehavior().add(
                    createChartBehaviorDto(community, chartId, validFrom, userStatusToChartTypeMapping.get(userStatus))
            );
        }

        return result;
    }

    private Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusMapping(BehaviorConfig behaviorConfig) {
        ChartUserStatusBehaviors chartUserStatusBehaviors = ChartUserStatusBehaviors.from(
                chartUserStatusBehaviorRepository.findByBehaviorConfig(behaviorConfig)
        );

        return chartUserStatusBehaviors.order();
    }

    private ChartBehaviorDto createChartBehaviorDto(String community, int chartId, Date since, ChartUserStatusBehavior chartUserStatusBehavior) {
        ChartBehaviorType chartBehaviorType = chartUserStatusBehavior.getChartBehavior().getType();
        String action = chartUserStatusBehavior.getAction();

        ChartBehaviorDto chartBehaviorDto = new ChartBehaviorDto(since, chartBehaviorType);
        if (chartUserStatusBehavior.isLocked()) {
            String actionUrl = deepLinkUrlFactory.createForChart(community, chartId, action);
            chartBehaviorDto.setLockedAction(actionUrl);
        }

        return chartBehaviorDto;
    }

    private ReferralsConditions getReferralsConditions(UserReferralsSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.isUnlimitedReferralsDuration()) {
                return new ReferralsUnlimitedConditions();
            } else {
                Date calculatedExpireDate = snapshot.getCalculatedExpireDate();
                if (calculatedExpireDate != null) {
                    return new ReferralsLimitedConditions(calculatedExpireDate);
                }
            }
        }

        return new ReferralsConditions();
    }
}
