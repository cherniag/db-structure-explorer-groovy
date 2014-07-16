package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum BadgeMappingRules {
    WIDE_RULES(ShapeType.WIDE, TypeToSubTypePair.LINK_INTERNAL_AD, TypeToSubTypePair.MEDIA_PLAYLIST, TypeToSubTypePair.MEDIA_TRACK),
    NARROW_RULES(ShapeType.NARROW, TypeToSubTypePair.LINK_INTERNAL_AD, TypeToSubTypePair.MEDIA_PLAYLIST, TypeToSubTypePair.MEDIA_TRACK);

    private ShapeType shapeType;
    private List<TypeToSubTypePair> typePairs;

    BadgeMappingRules(ShapeType shapeType, TypeToSubTypePair ... pairs) {
        this.shapeType = shapeType;
        this.typePairs = Arrays.asList(pairs);
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public List<TypeToSubTypePair> getTypePairs() {
        return new ArrayList<TypeToSubTypePair>(typePairs);
    }

    public static boolean allowed(ShapeType shapeType, ContentType contentType, Enum<?> subType) {
        for (BadgeMappingRules rule : values()) {
            if(rule.shapeType == shapeType) {
                for (TypeToSubTypePair typePair : rule.typePairs) {
                    if(typePair.getContentType() == contentType && typePair.getSubType() == subType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
