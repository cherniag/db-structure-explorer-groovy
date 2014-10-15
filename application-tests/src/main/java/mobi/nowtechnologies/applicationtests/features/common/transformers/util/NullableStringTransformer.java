package mobi.nowtechnologies.applicationtests.features.common.transformers.util;

import cucumber.api.Transformer;

public class NullableStringTransformer extends Transformer<NullableString> {
    @Override
    public NullableString transform(String value) {
        NullableString nullableString = new NullableString();

        if("<NULL>".equalsIgnoreCase(value)) {
            nullableString.v = null;
            return nullableString;
        }

        if("<EMPTY>".equalsIgnoreCase(value)) {
            nullableString.v = "";
            return nullableString;
        }

        nullableString.v = value;
        return nullableString;
    }

}
