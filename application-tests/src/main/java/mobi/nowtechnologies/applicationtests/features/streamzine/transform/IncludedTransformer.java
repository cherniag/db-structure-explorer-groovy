package mobi.nowtechnologies.applicationtests.features.streamzine.transform;

import cucumber.api.Transformer;

public class IncludedTransformer extends Transformer<Boolean> {

    @Override
    public Boolean transform(String value) {
        return "included".equalsIgnoreCase(value.trim());
    }
}
