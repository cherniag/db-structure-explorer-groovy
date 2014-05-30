package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.dto.streamzine.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StreamzineUpdateAsm {
    private DeepLinkUrlFactory deepLinkUrlFactory;

    public void setDeepLinkUrlFactory(DeepLinkUrlFactory deepLinkUrlFactory) {
        this.deepLinkUrlFactory = deepLinkUrlFactory;
    }

    public StreamzineUpdateDto convertOne(Update update) {
        StreamzineUpdateDto dto = new StreamzineUpdateDto(update.getDate().getTime());

        List<Block> blocks = update.getIncludedBlocks();
        Collections.sort(blocks, getComparator());
        for (Block block : blocks) {
            BaseContentItemDto contentItemDto = convertToContentItemDto(block);

            dto.addContentItem(contentItemDto);
            dto.addVisualBlock(convertToVisualBlock(block, contentItemDto));
        }

        return dto;
    }

    private BaseContentItemDto convertToContentItemDto(Block block) {
        DeeplinkInfo deeplinkInfo = block.getDeeplinkInfo();
        DeeplinkType deeplinkType = getDeeplinkType(block);

        if(deeplinkInfo instanceof ManualCompilationDeeplinkInfo) {
            IdListItemDto dto = new IdListItemDto(generateId(block), deeplinkType);
            dto.setImage(block.getCoverUrl());
            dto.setTitle(block.getTitle());
            dto.setSubTitle(block.getSubTitle());
            dto.setLinkValue(deepLinkUrlFactory.create((ManualCompilationDeeplinkInfo) deeplinkInfo));
            return dto;
        } else {
            DeeplinkValueItemDto dto = new DeeplinkValueItemDto(generateId(block), deeplinkType);
            dto.setImage(block.getCoverUrl());
            dto.setTitle(block.getTitle());
            dto.setSubTitle(block.getSubTitle());
            dto.setLinkValue(deepLinkUrlFactory.create(deeplinkInfo));
            return dto;
        }
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
