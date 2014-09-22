package mobi.nowtechnologies.applicationtests.features.common.transformers;

import cucumber.api.Transformer;

public class NullableStringTransformer extends Transformer<NullableString> {
    @Override
    public NullableString transform(String value) {
        NullableString nullableString = new NullableString();
        if(!"<NULL>".equalsIgnoreCase(value)) {
            nullableString.v = value;
        }
        return nullableString;
    }

}
