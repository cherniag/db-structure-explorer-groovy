package mobi.nowtechnologies.applicationtests.features.common.transformers;

public class NullableString {
    String v;

    public String value() {
        return v;
    }

    public boolean isNull() {
        return null == v;
    }
}
