package mobi.nowtechnologies.server.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.service.streamzine.DeepLinkInfoService;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType.*;

public enum ShapeTypeToContentTypeMapping {
    WIDE_RULES(ShapeType.WIDE, MUSIC, /*SOCIAL,*/ NEWS, PROMOTIONAL),
    NARROW_RULES(ShapeType.NARROW, MUSIC, /*SOCIAL,*/ NEWS, PROMOTIONAL),
    SLIM_BANNER_RULES(ShapeType.SLIM_BANNER, PROMOTIONAL),
    BUTTON_RULES(ShapeType.BUTTON, PROMOTIONAL);

    private final ShapeType shapeType;
    private final List<ContentType> contentTypes;

    ShapeTypeToContentTypeMapping(ShapeType shapeType, ContentType ... contentTypes) {
        this.shapeType = shapeType;
        this.contentTypes = Arrays.asList(contentTypes);

        Assert.notNull(contentTypes);
    }

    public boolean valid(DeepLinkInfoService.DeeplinkInfoData pair) {
        return shapeType == pair.getShapeType() && contentTypes.contains(pair.getContentType());
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public List<ContentType> getContentTypes() {
        return new ArrayList<ContentType>(contentTypes);
    }
}
