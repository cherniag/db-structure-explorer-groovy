package mobi.nowtechnologies.applicationtests.features.common.transformers.util;

public class NullableString {
    String v;

    public String value() {
        return v;
    }

    public boolean isNull() {
        return null == v;
    }

    @Override
    public String toString() {
        return v;
    }
}
