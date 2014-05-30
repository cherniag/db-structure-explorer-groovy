package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.domain.streamzine.ShapeTypeToContentTypeMapping;
import mobi.nowtechnologies.server.domain.streamzine.TypeToSubTypeMapping;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfoItem;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ExcludedSubTypes;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.ArrayList;
import java.util.List;

public class StreamzineTypesMappingService {

    public TypesMappingInfo getTypesMappingInfos() {
        TypesMappingInfo mappingInfo = new TypesMappingInfo();

        for (ShapeTypeToContentTypeMapping shapeTypeToContentMapping : ShapeTypeToContentTypeMapping.values()) {
            final ShapeType shapeType = shapeTypeToContentMapping.getShapeType();

            TypesMappingInfoItem info = new TypesMappingInfoItem(shapeType);
            for (final ContentType contentType : shapeTypeToContentMapping.getContentTypes()) {
                List<Enum<?>> all = TypeToSubTypeMapping.find(contentType).getSubTypes();
                all.removeAll(excluded(shapeType, contentType));

                info.add(contentType, all);
            }

            mappingInfo.getRules().add(info);
        }

        return mappingInfo;
    }

    private List<Enum<?>> excluded(ShapeType shapeType, ContentType contentType) {
        List<Enum<?>> all = new ArrayList<Enum<?>>();

        for (ExcludedSubTypes excludedSubTypes : ExcludedSubTypes.find(shapeType, contentType)) {
            all.addAll(excludedSubTypes.getValue());
        }

        return all;
    }
}
