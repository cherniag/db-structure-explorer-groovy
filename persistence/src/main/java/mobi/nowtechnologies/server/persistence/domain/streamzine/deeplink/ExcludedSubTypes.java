package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType.PROMOTIONAL;

public enum ExcludedSubTypes {
    BUTTON_PROMOTIONAL(ShapeType.BUTTON, PROMOTIONAL, LinkLocationType.EXTERNAL_AD);

    private final ShapeType shapeType;
    private final ContentType contentType;
    private List<Enum<?>> values;

    ExcludedSubTypes(ShapeType shapeType, ContentType contentType, Enum<?> ... values) {
        this.shapeType = shapeType;
        this.contentType = contentType;
        this.values = (values != null) ? Arrays.asList(values) : Collections.<Enum<?>>emptyList();
    }

    public static List<ExcludedSubTypes> find(ShapeType shapeType, ContentType contentType) {
        List<ExcludedSubTypes> found = new ArrayList<ExcludedSubTypes>();

        for (ExcludedSubTypes excluded : values()) {
            if(excluded.shapeType == shapeType && excluded.contentType == contentType) {
                found.add(excluded);
            }
        }

        return found;
    }

    public List<Enum<?>> getValue() {
        return values;
    }
}
