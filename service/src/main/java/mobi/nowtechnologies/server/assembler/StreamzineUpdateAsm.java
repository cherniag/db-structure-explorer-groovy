package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.dto.streamzine.AccessPolicyDto;
import mobi.nowtechnologies.server.dto.streamzine.BaseContentItemDto;
import mobi.nowtechnologies.server.dto.streamzine.DeeplinkType;
import mobi.nowtechnologies.server.dto.streamzine.DeeplinkValueItemDto;
import mobi.nowtechnologies.server.dto.streamzine.IdListItemDto;
import mobi.nowtechnologies.server.dto.streamzine.StreamzineUpdateDto;
import mobi.nowtechnologies.server.dto.streamzine.VisualBlock;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasSubTitle;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasTitle;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.StringUtils.isEmpty;

public class StreamzineUpdateAsm {
    private DeepLinkUrlFactory deepLinkUrlFactory;
    private DeepLinkInfoService deepLinkInfoService;
    private CommunityRepository communityRepository;
    private BadgesService badgesService;

    public void setDeepLinkUrlFactory(DeepLinkUrlFactory deepLinkUrlFactory) {
        this.deepLinkUrlFactory = deepLinkUrlFactory;
    }

    public void setDeepLinkInfoService(DeepLinkInfoService deepLinkInfoService) {
        this.deepLinkInfoService = deepLinkInfoService;
    }

    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public void setBadgesService(BadgesService badgesService) {
        this.badgesService = badgesService;
    }

    @Transactional(readOnly = true)
    public StreamzineUpdateDto convertOne(Update update, String community, Resolution resolution, boolean includePlayer) {
        StreamzineUpdateDto dto = new StreamzineUpdateDto(update.getDate().getTime());

        List<Block> blocks = update.getIncludedBlocks();
        Collections.sort(blocks, getComparator());
        for (Block block : blocks) {
            BaseContentItemDto contentItemDto = convertToContentItemDto(block, community, resolution, includePlayer);

            dto.addContentItem(contentItemDto);
            dto.addVisualBlock(convertToVisualBlock(block, contentItemDto));
        }

        return dto;
    }

    private BaseContentItemDto convertToContentItemDto(Block block, String community, Resolution resolution, boolean includePlayer) {
        Community c = communityRepository.findByName(community);

        DeeplinkInfo deeplinkInfo = block.getDeeplinkInfo();
        DeeplinkType deeplinkType = getDeeplinkType(block);

        if (deeplinkInfo instanceof ManualCompilationDeeplinkInfo) {
            IdListItemDto dto = new IdListItemDto(generateId(block), deeplinkType);
            dto.setLinkValue(deepLinkUrlFactory.create((ManualCompilationDeeplinkInfo) deeplinkInfo));

            assignValuesToItemDto(dto, block, c, resolution);

            return dto;
        } else {
            DeeplinkValueItemDto dto = new DeeplinkValueItemDto(generateId(block), deeplinkType);
            dto.setLinkValue(deepLinkUrlFactory.create(deeplinkInfo, c, includePlayer));

            assignValuesToItemDto(dto, block, c, resolution);

            return dto;
        }
    }

    private void assignValuesToItemDto(BaseContentItemDto dto, Block block, Community community, Resolution resolution) {
        DeeplinkInfo deeplinkInfo = block.getDeeplinkInfo();
        ShapeType shapeType = block.getShapeType();
        boolean allowedToAssignBadge = BadgeMappingRules.allowed(shapeType, deeplinkInfo.getContentType(), deepLinkInfoService.getSubType(deeplinkInfo));

        dto.setImage(block.getCoverUrl());
        if (allowedToAssignBadge && block.getBadgeId() != null) {
            dto.setBadgeIcon(badgesService.getBadgeFileName(block.getBadgeId(), community, resolution));
        }
        if (hasTitle(shapeType) && !isEmpty(block.getTitle())) {
            dto.setTitle(block.getTitle());
        }
        if (hasSubTitle(shapeType) && !isEmpty(block.getSubTitle())) {
            dto.setSubTitle(block.getSubTitle());
        }
    }

    private DeeplinkType getDeeplinkType(Block block) {
        DeeplinkType deeplinkType = DeeplinkType.DEEPLINK;
        if (block.getDeeplinkInfo() instanceof ManualCompilationDeeplinkInfo) {
            deeplinkType = DeeplinkType.ID_LIST;
        }
        return deeplinkType;
    }

    private String generateId(Block block) {
        return "content_item_id_" + block.getId();
    }

    private VisualBlock convertToVisualBlock(Block block, BaseContentItemDto contentItemDto) {
        VisualBlock visualBlock = new VisualBlock(block.getShapeType(), contentItemDto.getId());
        if (block.getAccessPolicy() != null) {
            visualBlock.setPolicyDto(convertToPolicyDto(block.getAccessPolicy()));
        }
        return visualBlock;
    }

    private AccessPolicyDto convertToPolicyDto(AccessPolicy accessPolicy) {
        AccessPolicyDto dto = new AccessPolicyDto(accessPolicy.getPermission());
        dto.getGrantedTo().addAll(accessPolicy.getGrantedToTypes());
        return dto;
    }

    private Comparator<Block> getComparator() {
        return new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                return o1.getPosition() - o2.getPosition();
            }
        };
    }


}
