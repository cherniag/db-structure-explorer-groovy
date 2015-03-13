package mobi.nowtechnologies.applicationtests.features.common.transformers.util;

public class NullableString {

    String v;

    public String value() {
        return v;
    }

    public <T extends Enum<T>> T value(final Class<T> type) {
        if (isNull()) {
            return null;
        } else {
            return Enum.valueOf(type, v);
        }
    }

    public <T extends Enum<T>> boolean belongs(final Class<T> type) {
        if (isNull()) {
            return false;
        }

        try {
            value(type);
            return true;
        } catch (IllegalArgumentException doesNotBelong) {
            return false;
        }
    }

    public boolean isNull() {
        return null == v;
    }

    @Override
    public String toString() {
        return v;
    }
}
