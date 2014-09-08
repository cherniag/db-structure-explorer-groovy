package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.dto.streamzine.*;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasSubTitle;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules.hasTitle;
import static org.springframework.util.StringUtils.isEmpty;

public class StreamzineUpdateAsm {
    private DeepLinkUrlFactory deepLinkUrlFactory;
    private DeepLinkInfoService deepLinkInfoService;
    private BadgeMappingRepository badgeMappingRepository;
    private CommunityRepository communityRepository;

    public void setDeepLinkUrlFactory(DeepLinkUrlFactory deepLinkUrlFactory) {
        this.deepLinkUrlFactory = deepLinkUrlFactory;
    }
    public void setDeepLinkInfoService(DeepLinkInfoService deepLinkInfoService) {
        this.deepLinkInfoService = deepLinkInfoService;
    }
    public void setBadgeMappingRepository(BadgeMappingRepository badgeMappingRepository) {
        this.badgeMappingRepository = badgeMappingRepository;
    }
    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Transactional(readOnly = true)
    public StreamzineUpdateDto convertOne(Update update, String community, String deviceType) {
        StreamzineUpdateDto dto = new StreamzineUpdateDto(update.getDate().getTime());

        List<Block> blocks = update.getIncludedBlocks();
        Collections.sort(blocks, getComparator());
        for (Block block : blocks) {
            BaseContentItemDto contentItemDto = convertToContentItemDto(block, community, deviceType);

            dto.addContentItem(contentItemDto);
            dto.addVisualBlock(convertToVisualBlock(block, contentItemDto));
        }

        return dto;
    }

    private BaseContentItemDto convertToContentItemDto(Block block, String community, String deviceType) {
        Community c = communityRepository.findByName(community);

        DeeplinkInfo deeplinkInfo = block.getDeeplinkInfo();
        DeeplinkType deeplinkType = getDeeplinkType(block);

        if(deeplinkInfo instanceof ManualCompilationDeeplinkInfo) {
            IdListItemDto dto = new IdListItemDto(generateId(block), deeplinkType);
            dto.setLinkValue(deepLinkUrlFactory.create((ManualCompilationDeeplinkInfo) deeplinkInfo));

            assignValuesToItemDto(dto, block, c, deviceType);

            return dto;
        } else {
            DeeplinkValueItemDto dto = new DeeplinkValueItemDto(generateId(block), deeplinkType);
            dto.setLinkValue(deepLinkUrlFactory.create(deeplinkInfo, community));

            assignValuesToItemDto(dto, block, c, deviceType);

            return dto;
        }
    }

    private void assignValuesToItemDto(BaseContentItemDto dto, Block block, Community community, String deviceType) {
        DeeplinkInfo deeplinkInfo = block.getDeeplinkInfo();
        ShapeType shapeType = block.getShapeType();
        boolean allowedToAssignBadge =
                BadgeMappingRules.allowed(shapeType, deeplinkInfo.getContentType(), deepLinkInfoService.getSubType(deeplinkInfo));

        dto.setImage(block.getCoverUrl());
        if(allowedToAssignBadge && block.getBadgeId() != null) {
            List<BadgeMapping> badgesInfo = badgeMappingRepository.findByCommunityAndDeviceType(community, deviceType, block.getBadgeId());
            dto.setBadgeIcon(createBadgeInfo(badgesInfo));
        }
        if(hasTitle(shapeType) && !isEmpty(block.getTitle())){
            dto.setTitle(block.getTitle());
        }
        if(hasSubTitle(shapeType) && !isEmpty(block.getSubTitle())) {
            dto.setSubTitle(block.getSubTitle());
        }
    }

    private List<BadgeInfo> createBadgeInfo(List<BadgeMapping> mappings) {
        List<BadgeInfo> badgeInfo = new ArrayList<BadgeInfo>();
        for (BadgeMapping mapping : mappings) {
            // omit if uesr did not assigned the picture for this resolution
            // but only assigned the size (resolution) of the picture
            FilenameAlias filenameAlias = mapping.getFilenameAlias();
            if(filenameAlias != null) {
                badgeInfo.add(new BadgeInfo(mapping.getFilenameAlias().getFileName(), mapping.getResolution().getWidth(), mapping.getResolution().getHeight()));
            }
        }
        return badgeInfo;
    }

    private DeeplinkType getDeeplinkType(Block block) {
        DeeplinkType deeplinkType = DeeplinkType.DEEPLINK;
        if (block.getDeeplinkInfo() instanceof ManualCompilationDeeplinkInfo){
            deeplinkType = DeeplinkType.ID_LIST;
        }
        return deeplinkType;
    }

    private String generateId(Block block) {
        return "content_item_id_" + block.getId();
    }

    private VisualBlock convertToVisualBlock(Block block, BaseContentItemDto contentItemDto) {
        VisualBlock visualBlock = new VisualBlock(block.getShapeType(), contentItemDto.getId());
        if(block.getAccessPolicy() != null) {
            visualBlock.setPolicyDto(convertToPolicyDto(block.getAccessPolicy()));
        }
        return visualBlock;
    }

    private AccessPolicyDto convertToPolicyDto(AccessPolicy accessPolicy) {
        AccessPolicyDto dto = new AccessPolicyDto(accessPolicy.getPermission());
        dto.getGrantedTo().addAll(accessPolicy.getUserStatusTypes());
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
