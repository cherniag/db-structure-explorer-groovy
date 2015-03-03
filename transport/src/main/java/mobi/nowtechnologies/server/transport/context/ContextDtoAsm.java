package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehavior;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;
import mobi.nowtechnologies.server.service.behavior.BehaviorInfoService;
import mobi.nowtechnologies.server.service.behavior.ChartBehaviorInfo;
import mobi.nowtechnologies.server.service.behavior.ChartBehaviorService;
import mobi.nowtechnologies.server.service.behavior.UserStatusTypeService;
import mobi.nowtechnologies.server.transport.context.dto.BehaviorTemplateDto;
import mobi.nowtechnologies.server.transport.context.dto.ChartBehaviorDto;
import mobi.nowtechnologies.server.transport.context.dto.ChartBehaviorsDto;
import mobi.nowtechnologies.server.transport.context.dto.ContextDto;
import mobi.nowtechnologies.server.transport.context.dto.InstructionDto;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

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
    BehaviorInfoService behaviorInfoService;
    @Resource
    TimeService timeService;
    @Resource
    ChartBehaviorService chartBehaviorService;

    //
    // API
    //
    public ContextDto assemble(User user, boolean needToLookAtActivationDate) {
        if (!needToLookAtActivationDate && behaviorInfoService.isFirstDeviceLoginBeforeReferralsActivation(user)) {
            // we do not create snapshots and thus can't move forward
            return ContextDto.empty();
        }

        Community community = user.getCommunity();

        BehaviorConfig behaviorConfig = behaviorInfoService.getBehaviorConfig(needToLookAtActivationDate, community);
        UserReferralsSnapshot snapshot = behaviorInfoService.getUserReferralsSnapshot(user, behaviorConfig);

        Date serverTime = timeService.now();
        List<Pair<UserStatusType, Date>> userStatusTypeDateMap = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        ContextDto context = ContextDto.normal(serverTime);
        context.getReferralsContextDto().fill(snapshot);
        fillChartsTemplatesInfo(context, behaviorConfig);

        context.getFavoritesContextDto().getInstructions().addAll(calcFavouritesInstructions(behaviorConfig, userStatusTypeDateMap));
        context.getAdsContextDto().getInstructions().addAll(calcAdsInstructions(behaviorConfig, userStatusTypeDateMap));
        context.getChartContextDto().getChartBehaviors().addAll(calcChartBehaviors(chartBehaviorService.info(behaviorConfig, userStatusTypeDateMap, snapshot, user, serverTime)));

        return context;
    }

    private void fillChartsTemplatesInfo(ContextDto context, BehaviorConfig behaviorConfig) {
        for (ChartBehaviorType chartBehaviorType : ChartBehaviorType.values()) {
            ChartBehavior chartBehavior = behaviorConfig.getChartBehavior(chartBehaviorType);

            BehaviorTemplateDto behaviorTemplateDto = context.getChartContextDto().getChartTemplateBehaviorsDto(chartBehaviorType);
            behaviorTemplateDto.fill(chartBehavior);
        }
    }

    private List<InstructionDto> calcFavouritesInstructions(BehaviorConfig behaviorConfig, List<Pair<UserStatusType, Date>> userStatusTypeDatePairs) {
        List<InstructionDto> instructions = new ArrayList<>(2);

        for (Map.Entry<UserStatusType, Date> statusTypeDateEntry : userStatusTypeDatePairs) {
            UserStatusType statusType = statusTypeDateEntry.getKey();
            Date since = statusTypeDateEntry.getValue();

            boolean favouritesOff = behaviorConfig.getContentUserStatusBehavior(statusType).isFavoritesOff();
            instructions.add(new InstructionDto(favouritesOff, since));
        }

        return instructions;
    }

    private List<InstructionDto> calcAdsInstructions(BehaviorConfig behaviorConfig, List<Pair<UserStatusType, Date>> userStatusTypeDatePairs) {
        List<InstructionDto> instructions = new ArrayList<>(2);

        for (Map.Entry<UserStatusType, Date> statusTypeDateEntry : userStatusTypeDatePairs) {
            UserStatusType statusType = statusTypeDateEntry.getKey();
            boolean addsOff = behaviorConfig.getContentUserStatusBehavior(statusType).isAddsOff();
            instructions.add(new InstructionDto(addsOff, statusTypeDateEntry.getValue()));
        }

        return instructions;
    }

    private List<ChartBehaviorsDto> calcChartBehaviors(Map<Integer, Collection<ChartBehaviorInfo>> infos) {
        List<ChartBehaviorsDto> chartBehaviors = new ArrayList<>();

        for (Map.Entry<Integer, Collection<ChartBehaviorInfo>> info : infos.entrySet()) {
            ChartBehaviorsDto chartBehaviorsDto = new ChartBehaviorsDto(info.getKey());
            for (ChartBehaviorInfo chartBehaviorInfo : info.getValue()) {
                chartBehaviorsDto.getBehavior().add(new ChartBehaviorDto(chartBehaviorInfo));
            }

            chartBehaviors.add(chartBehaviorsDto);
        }

        return chartBehaviors;
    }
}
