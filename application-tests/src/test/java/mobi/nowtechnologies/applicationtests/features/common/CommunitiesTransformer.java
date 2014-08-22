package mobi.nowtechnologies.applicationtests.features.common;

import cucumber.api.Transformer;
import mobi.nowtechnologies.applicationtests.services.helper.transformer.DataLoader;

import java.util.List;

public class CommunitiesTransformer extends Transformer<List<String>> {
    private final String location;

    public CommunitiesTransformer() {
        location = "features/common/communities.txt";
    }

    @Override
    public List<String> transform(String value) {
        return DataLoader.loadCommunites(location);
    }
}
