package mobi.nowtechnologies.server.domain.streamzine.rules;

import mobi.nowtechnologies.server.domain.streamzine.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

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
}
