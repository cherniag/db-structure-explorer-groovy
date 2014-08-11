package mobi.nowtechnologies.applicationtests.features.social.email.transformers;

import cucumber.api.Transformer;
import mobi.nowtechnologies.applicationtests.services.helper.transformer.DataLoader;

import java.util.List;

public class CommunityTransformer extends Transformer<List<String>> {
    private final String location;

    public CommunityTransformer() {
        location = "features/email/communities.txt";
    }

    @Override
    public List<String> transform(String value) {
        return DataLoader.loadCommunites(location);
    }
}
