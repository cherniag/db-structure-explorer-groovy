package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

public interface DeeplinkInfoData {

    ShapeType getShapeType();

    ContentType getContentType();

    String getKey();

    String getValue();
}
