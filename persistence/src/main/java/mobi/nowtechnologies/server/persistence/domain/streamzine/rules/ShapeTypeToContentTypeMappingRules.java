package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType.MUSIC;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType.NEWS;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType.PROMOTIONAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

public enum ShapeTypeToContentTypeMappingRules {
    WIDE_RULES(ShapeType.WIDE, MUSIC, /*SOCIAL,*/ NEWS, PROMOTIONAL),
    NARROW_RULES(ShapeType.NARROW, MUSIC, /*SOCIAL,*/ NEWS, PROMOTIONAL),
    SLIM_BANNER_RULES(ShapeType.SLIM_BANNER, PROMOTIONAL);

    private final ShapeType shapeType;
    private final List<ContentType> contentTypes;

    ShapeTypeToContentTypeMappingRules(ShapeType shapeType, ContentType... contentTypes) {
        this.shapeType = shapeType;
        this.contentTypes = Arrays.asList(contentTypes);

        Assert.notNull(contentTypes);
    }

    public boolean valid(DeeplinkInfoData pair) {
        return shapeType == pair.getShapeType() && contentTypes.contains(pair.getContentType());
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public List<ContentType> getContentTypes() {
        return new ArrayList<ContentType>(contentTypes);
    }
}
