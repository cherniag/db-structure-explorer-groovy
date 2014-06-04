package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfoItem;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.ShapeTypeToContentTypeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.List;

public class StreamzineTypesMappingService {

    public TypesMappingInfo getTypesMappingInfos() {
        TypesMappingInfo mappingInfo = new TypesMappingInfo();

        for (ShapeTypeToContentTypeMappingRules shapeTypeToContentMapping : ShapeTypeToContentTypeMappingRules.values()) {
            final ShapeType shapeType = shapeTypeToContentMapping.getShapeType();

            TypesMappingInfoItem info = new TypesMappingInfoItem(shapeType);
            for (final ContentType contentType : shapeTypeToContentMapping.getContentTypes()) {
                List<Enum<?>> all = TypeToSubTypePair.getAllSubTypesByContentType(contentType);

                info.add(contentType, all);
            }

            mappingInfo.getRules().add(info);
        }

        return mappingInfo;
    }
}
