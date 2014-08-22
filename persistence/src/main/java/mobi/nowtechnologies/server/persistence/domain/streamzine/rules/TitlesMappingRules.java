package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

public enum TitlesMappingRules {
    WIDE_RULES(ShapeType.WIDE, true, true),
    NARROW_RULES(ShapeType.NARROW, true, false),
    SLIM_RULES(ShapeType.SLIM_BANNER, false, false);

    private final ShapeType shapeType;
    private final boolean title;
    private final boolean subTitle;

    TitlesMappingRules(ShapeType shapeType, boolean title, boolean subTitle) {
        this.shapeType = shapeType;
        this.title = title;
        this.subTitle = subTitle;
    }

    public static boolean hasTitle(ShapeType shapeType) {
        return find(shapeType).title;
    }

    public static boolean hasSubTitle(ShapeType shapeType) {
        return find(shapeType).subTitle;
    }

    private static TitlesMappingRules find(ShapeType shapeType) {
        for (TitlesMappingRules titlesMappingRule : values()) {
            if(titlesMappingRule.shapeType == shapeType) {
                return titlesMappingRule;
            }
        }
        throw new IllegalArgumentException("Unknown shapeType: " + shapeType);
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public boolean isTitle() {
        return title;
    }

    public boolean isSubTitle() {
        return subTitle;
    }

    @Override
    public String toString() {
        return "TitlesMappingRules{" +
                "shapeType=" + shapeType +
                ", has title=" + title +
                ", has subTitle=" + subTitle +
                '}';
    }
}
