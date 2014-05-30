package mobi.nowtechnologies.server.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypesMappingInfoItem {
    private ShapeType shapeType;

    private Map<ContentType, List<Enum<?>>> typeWithSubtypes = new HashMap<ContentType, List<Enum<?>>>();

    public TypesMappingInfoItem(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public void add(ContentType contentType, List<Enum<?>> subTypes) {
        typeWithSubtypes.put(contentType, subTypes);
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public Map<ContentType, List<Enum<?>>> getTypeWithSubtypes() {
        return Collections.unmodifiableMap(typeWithSubtypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypesMappingInfoItem that = (TypesMappingInfoItem) o;

        if (shapeType != that.shapeType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return shapeType != null ? shapeType.hashCode() : 0;
    }
}
