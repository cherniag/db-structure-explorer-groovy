package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

public abstract class BlockDto {
    private boolean included;
    private ShapeType shapeType;

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public abstract int getPosition();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockDto blockDto = (BlockDto) o;

        if (getPosition() != blockDto.getPosition()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getPosition();
    }
}
