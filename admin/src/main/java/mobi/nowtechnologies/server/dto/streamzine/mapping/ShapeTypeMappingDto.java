package mobi.nowtechnologies.server.dto.streamzine.mapping;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import java.util.ArrayList;
import java.util.List;

public class ShapeTypeMappingDto {

    private String shapeType;
    private List<ContentWithSubTypesMappingDto> dtos = new ArrayList<ContentWithSubTypesMappingDto>();

    public String getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType.name();
    }

    public List<ContentWithSubTypesMappingDto> getDtos() {
        return dtos;
    }

    @Override
    public String toString() {
        return "ShapeTypeMappingDto{" +
               ", shapeType=" + shapeType +
               ", dtos=" + dtos +
               '}';
    }
}
