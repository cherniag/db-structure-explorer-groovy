package mobi.nowtechnologies.applicationtests.features.common.transformers.list;

import cucumber.api.Transformer;

public class ListValuesTransformer extends Transformer<ListValues> {
    @Override
    public ListValues transform(String value) {
        return ListValues.from(value);
    }
}
